package org.formatko.skygram;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.call.CallReceivedEvent;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.formatting.Text;
import org.formatko.skygram.model.Store;
import org.formatko.skygram.model.User;
import org.formatko.skygram.store.FileStoreHandler;
import org.formatko.skygram.store.MessageCache;
import org.formatko.skygram.store.StoreHandler;
import org.formatko.skygram.util.SkypeUtils;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pro.zackpollard.telegrambot.api.chat.ChatType.PRIVATE;

/**
 * Class of org.formatko.skygram
 *
 * @author aivanov
 */
public class Skygram {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    private TelegramBot bot;
    private Store store;
    private String botKey;
    private StoreHandler storeHandler;

    private MessageCache messageCache = new MessageCache();

    private Map<User, Skype> userSkypeCache = new HashMap<>();

    public Skygram(String botKey) {
        this.botKey = botKey;
        this.storeHandler = new FileStoreHandler();
    }

    public void start() {
        logger.info("Starting...");
        store = storeHandler.load();

        bot = TelegramBot.login(botKey);
        if (bot == null) {
            throw new RuntimeException("Can't create Telegram bot. Check key of bot.");
        }
        bot.getEventsManager().register(new pro.zackpollard.telegrambot.api.event.Listener() {
            @Override
            public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
                if (PRIVATE.equals(event.getChat().getType())) {
                    try {
                        String command = event.getCommand().toLowerCase();
                        if (command.equals("login")) {
                            String[] args = event.getArgs();
                            if (args.length == 2) {
                                boolean success;
                                String pass = Base64.getEncoder().encodeToString(args[1].getBytes());
                                User user = new User(((pro.zackpollard.telegrambot.api.chat.IndividualChat) event.getChat()).getPartner().getId(), args[0], pass);
                                store.addUser(user);
                                storeHandler.save(store);
                                success = startSkype(user, createSkype(user));
                                if (success) {
                                    event.getChat().sendMessage("Successfully authorised with skype.");
                                    logger.info("New logining: " + args[0]);
                                }
                            } else {
                                event.getChat().sendMessage("Correct usage is: /login [username] [password]");
                            }
                        }
                        if (command.equals("logout")) {
                            User user = new User(((pro.zackpollard.telegrambot.api.chat.IndividualChat) event.getChat()).getPartner().getId(), null, null);
                            store.removeUser(user);
                            storeHandler.save(store);
                            Skype skype = userSkypeCache.get(user);
                            skype.logout();
                            userSkypeCache.remove(user);
                            event.getChat().sendMessage("Successfully logout from skype.");
                            logger.info("New logouting: " + user.getTgUserId());
                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "OnCommand error", e);
                    }
                }
            }

            @Override
            public void onTextMessageReceived(pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent event) {
                if (PRIVATE.equals(event.getChat().getType())) {
                    Message repliedTo = event.getMessage().getRepliedTo();
                    if (repliedTo != null) {
                        com.samczsun.skype4j.chat.Chat chat = messageCache.getChat(repliedTo);
                        if (chat != null) {
                            try {
                                chat.sendMessage(com.samczsun.skype4j.formatting.Message.create().with(Text.plain(event.getContent().getContent())));
                            } catch (ConnectionException e) {
                                logger.log(Level.SEVERE, "Can't send message in chat " + chat.getIdentity(), e);
                            }
                        } else {
                            logger.warning("Can't find chat for " + repliedTo.toString());
                        }
                    }
                    //logger.log(Level.INFO, messageCache.toString());
                }
            }
        });

        for (User user : store.getUsers()) {
            userSkypeCache.put(user, createSkype(user));
        }

        for (Map.Entry<User, Skype> pair : userSkypeCache.entrySet()) {
            startSkype(pair.getKey(), pair.getValue());
        }

        bot.startUpdates(false);
        logger.info("Started...");
    }

    private Skype createSkype(User user) {
        return new SkypeBuilder(user.getSkLogin(), new String(Base64.getDecoder().decode(user.getSkPassword()))).withLogger(logger).withAllResources().build();
    }

    private boolean startSkype(User user, Skype skype) {
        try {
            skype.login();
            skype.getEventDispatcher().registerListener(new Listener() {
                Chat tgChat = null;

                private Chat getTgChat() {
                    if (tgChat == null) {
                        tgChat = bot.getChat(user.getTgUserId());
                    }
                    return tgChat;
                }

                @EventHandler
                public void onMessageReceived(MessageReceivedEvent e) throws ConnectionException {
                    String chatName = SkypeUtils.getChatName(e);
                    boolean isGroup = SkypeUtils.isGroup(e);
                    String senderName = e.getMessage().getSender().getDisplayName();
                    String textMessage = e.getMessage().getContent().asPlaintext();
                    Message message = bot.sendMessage(getTgChat(),
                            SendableTextMessage.builder().message(
                                    (isGroup ? "`" + chatName + "`\n" : "") +
                                            "*" + senderName + "*: " +
                                            textMessage)
                                    .parseMode(ParseMode.MARKDOWN).build());
                    messageCache.addMessage(e.getChat(), message);
                }

                @EventHandler
                public void onCallReceived(CallReceivedEvent e) throws ConnectionException {
                    String chatName = SkypeUtils.getChatName(e);
                    boolean isGroup = SkypeUtils.isGroup(e);
                    String senderName = e.getSender().getDisplayName();
                    Message message = bot.sendMessage(getTgChat(),
                            SendableTextMessage.builder().message(
                                    (isGroup ? "`" + chatName + "`\n" : "") +
                                            "*" + senderName + "* " + (e.isCallStarted() ? "start calling." : "stop calling."))
                                    .parseMode(ParseMode.MARKDOWN).build());
                    messageCache.addMessage(e.getChat(), message);
                }
            });
            skype.subscribe();
            return true;
        } catch (InvalidCredentialsException e) {
            logger.log(Level.SEVERE, "Can't log in with " + user, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't start skype with " + user, e);
        }
        return false;
    }
}
