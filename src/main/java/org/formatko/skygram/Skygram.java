package org.formatko.skygram;

import fr.delthas.skype.*;
import fr.delthas.skype.message.AbstractMessage;
import fr.delthas.skype.message.Message;
import fr.delthas.skype.message.TextMessage;
import org.formatko.skygram.model.Store;
import org.formatko.skygram.store.FileStoreHandler;
import org.formatko.skygram.store.StoreHandler;
import org.formatko.skygram.util.MessageStack;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantJoinGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.ParticipantLeaveGroupChatEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.formatko.skygram.util.TgUtils.*;
import static org.formatko.skygram.util.TgUtils.arrayToString;
import static pro.zackpollard.telegrambot.api.chat.ChatType.PRIVATE;

/**
 * Class of skygram. Have the logic of app
 *
 * @author aivanov
 */
public class Skygram {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    private String botKey;
    private String botUserName;
    private StoreHandler storeHandler;

    private Group skChat;
    private Chat tgChat;

    private Pattern pattern;

    MessageStack cache = new MessageStack();

    public Skygram(String botKey, String storePath) {
        this.botKey = botKey;
        this.storeHandler = new FileStoreHandler(storePath);
    }

    public Boolean isMatch(String html) {
        return pattern != null && pattern.matcher(html.toLowerCase()).matches();
    }

    public void start() throws Exception {
        logger.info("Starting...");
        Store store = storeHandler.load();

        if (store.isMatchWordsEnabled()) {
            String patterString = "";
            String[] words = store.getMatchWords();
            for (int i = 0; i < words.length; i++) {
                patterString += ".*" + words[i].toLowerCase() + ".*";
                if (i != words.length - 1) {
                    patterString += "|";
                }
            }
            if (!patterString.isEmpty()) {
                pattern = Pattern.compile(patterString, Pattern.DOTALL);
            }
        }


        TelegramBot bot = TelegramBot.login(botKey);
        botUserName = "@" + bot.getBotUsername();

        Skype skype = new Skype(store.getSkLogin(), store.getSkPassword());

        if (bot == null) {
            throw new RuntimeException("Can't create Telegram bot. Check key of bot.");
        }

        bot.getEventsManager().register(new pro.zackpollard.telegrambot.api.event.Listener() {
            @Override
            public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
                if (PRIVATE.equals(event.getChat().getType())) {
                    try {
//                        String command = event.getCommand().toLowerCase();
//                        if (command.equals("login")) {
//                            String[] args = event.getArgs();
//                            if (args.length == 2) {
//                                boolean success;
//                                ChatLink chatLink = new ChatLink(((pro.zackpollard.telegrambot.api.chat.IndividualChat) event.getChat()).getPartner().getId(), args[0], encrypt(args[1]));
//                                store.addUser(chatLink);
//                                storeHandler.save(store);
//                                success = startSkype(chatLink, createSkype(chatLink));
//                                if (success) {
//                                    event.getChat().sendMessage("Successfully authorised with skype.");
//                                    logger.info("New logining: " + args[0]);
//                                }
//                            } else {
//                                event.getChat().sendMessage("Correct usage is: /login [username] [password]");
//                            }
//                        }
//                        if (command.equals("logout")) {
//                            ChatLink chatLink = new ChatLink(((pro.zackpollard.telegrambot.api.chat.IndividualChat) event.getChat()).getPartner().getId());
//                            store.removeUser(chatLink);
//                            storeHandler.save(store);
//                            Skype skype = userSkypeCache.findSkypeMessage(chatLink);
//                            skype.logout();
//                            userSkypeCache.remove(chatLink);
//                            event.getChat().sendMessage("Successfully logout from skype.");
//                            logger.info("New logouting: " + chatLink.getTgChatId());
//                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "OnCommand error", e);
                    }
                }
            }

            @Override
            public void onTextMessageReceived(pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent event) {
                if (event.getChat().getId().equals(tgChat.getId())) {
                    String content = event.getContent().getContent();
                    pro.zackpollard.telegrambot.api.chat.message.Message tgMessage = event.getMessage();
                    if (isReplyToBot(tgMessage) || content.startsWith(botUserName)) {
                        if (content.startsWith(botUserName)) {
                            content = content.replace(botUserName, "");
                        }
                        String username = b(tgMessage.getSender().getFullName());
                        TextMessage textMessage = new TextMessage(null, username + ": " + content);
                        skChat.sendMessage(textMessage);
                        cache.add(event.getMessage(), textMessage);
                    }
                }
            }

            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                logger.info(event.getMessage().asJson().toString());
            }

            @Override
            public void onMessageEditReceived(MessageEditReceivedEvent event) {
                logger.info(event.getMessage().asJson().toString());
            }

            @Override
            public void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
                logger.info(event.getMessage().asJson().toString());
                pro.zackpollard.telegrambot.api.chat.message.Message tgMessage = event.getMessage();
                if (isReplyToBot(tgMessage)) {
                    String username = b(tgMessage.getSender().getFullName());
                    String caption = event.getContent().getCaption();
                    TextMessage textMessage = new TextMessage(null, username + i(" отправил фотку") + (caption != null ? (" c подписью '" + caption + "'") : ""));
                    skChat.sendMessage(textMessage);
                    cache.add(event.getMessage(), textMessage);
                }
            }

            @Override
            public void onStickerMessageReceived(StickerMessageReceivedEvent event) {
                logger.info(event.getMessage().asJson().toString());
                pro.zackpollard.telegrambot.api.chat.message.Message tgMessage = event.getMessage();
                if (isReplyToBot(tgMessage)) {
                    String username = b(tgMessage.getSender().getFullName());
                    String emoji = event.getContent().getContent().getEmoji();
                    TextMessage textMessage = new TextMessage(null, username + i(" отправил стикер" + emoji));
                    skChat.sendMessage(textMessage);
                    cache.add(event.getMessage(), textMessage);
                }
            }

            @Override
            public void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
                logger.info(event.getMessage().asJson().toString());

                TextMessage textMessage = new TextMessage(null, i("В чате Telegram пополнение: " + event.getParticipant().getFullName()));
                skChat.sendMessage(textMessage);
                cache.add(event.getMessage(), textMessage);
            }

            @Override
            public void onParticipantLeaveGroupChat(ParticipantLeaveGroupChatEvent event) {
                logger.info(event.getMessage().asJson().toString());

                TextMessage textMessage = new TextMessage(null, i("В чате Telegram убавление: " + event.getParticipant().getFullName()));
                skChat.sendMessage(textMessage);
                cache.add(event.getMessage(), textMessage);
            }
        });

        skype.connect();

        skype.setErrorListener(e -> logger.log(Level.SEVERE, "Error", e));

        skype.addUserMessageListener(new UserMessageListener() {
            Random rand = new Random(System.currentTimeMillis());

            @Override
            public void messageReceived(User sender, Message message) {
                logger.info("from '" + sender.getDisplayName() + "': " + message);
                switch (message.getType()) {
                    case TEXT:
                        break;
                    case PICTURE:
                        break;
                    case FILE:
                        break;
                    case VIDEO:
                        break;
                    case CONTACT:
                        break;
                    case MOJI:
                        break;
                    case UNKNOWN:
                        break;
                }
                sender.sendMessage(Arrays.asList("Прости, но я занят.", "Давай потом", "Так так так...", "Хм..").get(rand.nextInt(4)));
            }

            @Override
            public void messageEdited(User sender, Message message) {
                logger.info("from '" + sender.getDisplayName() + "': " + message);
            }

            @Override
            public void messageRemoved(User sender, Message message) {
                logger.info("from '" + sender.getDisplayName() + "': " + message);
                sender.sendMessage("Чоит ты там удаляешь? :)");
            }
        });

        for (Group group : skype.getGroups()) {
            if (group.getId().equals(store.getSkChatId())) {
                this.skChat = group;
            }
        }
        tgChat = bot.getChat(store.getTgChatId());

        skype.addGroupMessageListener(new GroupMessageListener() {
            @Override
            public void messageReceived(Group group, User sender, Message message) {
                try {
                    logger.info("g '" + group.getTopic() + "' from " + sender.getDisplayName() + ": " + message);
                    if (Objects.equals(group.getId(), skChat.getId())) {
                        if (!store.isMatchWordsEnabled() || isMatch(((AbstractMessage) message).getHtml())) {
                            String messageToTg = "";
                            String senderName = b(sender.getDisplayName());
                            switch (message.getType()) {
                                case TEXT:
                                    TextMessage text = (TextMessage) message;
                                    if (text.hasQuotes()) {
                                        String textToQuote = prepareQuotes(text.getHtml());
                                        messageToTg = senderName + ":\n" + textToQuote;
                                        logger.info(messageToTg);
                                    } else {
                                        messageToTg = senderName + (text.isMe() ? "" : ": ") + sanitize(text.getHtml());
                                    }
                                    break;
                                case PICTURE:
                                    messageToTg = senderName + i(" запостил картинку. \n") + pre("Вот нет чтобы ссылкой скинуть.");
                                    break;
                                case FILE:
                                    messageToTg = senderName + i(" запостил файл. \n") + pre("Использует файлообменник на всю катушку.");
                                    break;
                                case VIDEO:
                                    messageToTg = senderName + i(" прислал видеообращение. \n") + pre("Молодец, но мог бы просто текстом..");
                                    break;
                                case CONTACT:
                                    messageToTg = senderName + i(" поделился данными контакта. \n") + pre("Молодец.");
                                    break;
                                case MOJI:
                                    messageToTg = senderName + i(" прислал Можи. Какой мовитон...\n") + pre("Это такие стикеры в скайпе");
                                    break;
                                case UNKNOWN:
                                    messageToTg = senderName + i(" непонятно что прислал. \n") + pre("Надо подробно разобраться");
                                    break;
                            }
                            pro.zackpollard.telegrambot.api.chat.message.Message mes = tgChat.sendMessage(html(messageToTg));
                            cache.add(mes, message);
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error: ", e);
                }
            }

            @Override
            public void messageEdited(Group group, User sender, Message message) {
                try {
                    logger.info("g '" + group.getTopic() + "' from " + sender.getDisplayName() + ": " + message);
                    if (Objects.equals(group.getId(), skChat.getId())) {
                        pro.zackpollard.telegrambot.api.chat.message.Message tgMessage = cache.findTgMessage(message);
                        if (tgMessage != null) {
                            String messageToTg = "";
                            String senderName = b(sender.getDisplayName());
                            switch (message.getType()) {
                                case TEXT:
                                    TextMessage text = (TextMessage) message;
                                    if (text.hasQuotes()) {
                                        String textToQuote = prepareQuotes(text.getHtml());
                                        messageToTg = senderName + ":\n" + textToQuote;
                                        logger.info(messageToTg);
                                    } else {
                                        messageToTg = senderName + (text.isMe() ? "" : ": ") + sanitize(text.getHtml());
                                    }
                                    pro.zackpollard.telegrambot.api.chat.message.Message editMessage = tgChat.getBotInstance().editMessageText(tgChat.getId(), tgMessage.getMessageId(), messageToTg, ParseMode.HTML, false, null);
                                    cache.add(editMessage, message);
                                    break;
                                case PICTURE:
                                case FILE:
                                case VIDEO:
                                case CONTACT:
                                case MOJI:
                                case UNKNOWN:
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error: ", e);
                }
            }

            @Override
            public void messageRemoved(Group group, User sender, Message message) {
                try {
                    logger.info("g '" + group.getTopic() + "' from " + sender.getDisplayName() + ": " + message);
                    if (Objects.equals(group.getId(), skChat.getId())) {
                        pro.zackpollard.telegrambot.api.chat.message.Message tgMessage = cache.findTgMessage(message);
                        if (tgMessage != null) {
                            String messageToTg = b(sender.getDisplayName());
                            messageToTg = messageToTg + i(" удалил сообщение");
                            tgChat.getBotInstance().editMessageText(tgChat.getId(), tgMessage.getMessageId(), messageToTg, ParseMode.HTML, false, null);
                            cache.remove(tgMessage);
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error: ", e);
                }
            }
        });


        skype.addGroupPropertiesListener(new GroupPropertiesListener() {
            @Override
            public void usersAdded(Group group, List<User> list) {
                if (Objects.equals(group.getId(), skChat.getId())) {
                    List<String> nameStrings = new ArrayList<>();
                    for (User user : list) {
                        nameStrings.add(user.getDisplayName());
                    }
                    String names = arrayToString(nameStrings.toString(), null);

                    tgChat.sendMessage("Пополнение: " + names);
                    String greetings = store.getGreetings();
                    if (greetings != null && !greetings.isEmpty()) {
                        String words = arrayToString(Arrays.toString(store.getMatchWords()), " ");
                        String s = greetings.replace("{newMembers}", names).replace("{filters}", (pattern != null ? words : "Например пицц"));
                        skChat.sendMessage(new TextMessage(null, s));
                    }
                }
            }

            @Override
            public void usersRemoved(Group group, List<User> list) {
                if (Objects.equals(group.getId(), skChat.getId())) {
                    List<String> strings = new ArrayList<>();
                    for (User user : list) {
                        strings.add(user.getDisplayName());
                    }
                    tgChat.sendMessage("Убавление: " + arrayToString(strings.toString(), null));
                }
            }

            @Override
            public void topicChanged(Group group, String s) {
                if (Objects.equals(group.getId(), skChat.getId())) {
                    tgChat.sendMessage("Тему чата в скайпе поменяли: " + s);
                }
            }

            @Override
            public void usersRolesChanged(Group group, List<Pair<User, Role>> list) {
                if (Objects.equals(group.getId(), skChat.getId())) {
                    List<String> strings = new ArrayList<>();
                    for (Pair<User, Role> pair : list) {
                        strings.add(pair.getFirst().getDisplayName() + " теперь " + (pair.getSecond() == Role.ADMIN ? "админ" : "обычный пользователь"));
                    }
                    tgChat.sendMessage("Обновили ролей пользователей: " + strings);
                }
            }
        });

        bot.startUpdates(false);
        logger.info("Started...");
    }

    private boolean isReplyToBot(pro.zackpollard.telegrambot.api.chat.message.Message tgMessage) {
        return tgMessage.getRepliedTo() != null && botUserName.equals(tgMessage.getRepliedTo().getSender().getUsername());

    }

}
