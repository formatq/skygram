package org.formatko.skygram.util;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import javax.xml.transform.TransformerException;
import java.util.logging.Logger;

import static pro.zackpollard.telegrambot.api.chat.message.send.ParseMode.HTML;
import static pro.zackpollard.telegrambot.api.chat.message.send.ParseMode.MARKDOWN;

/**
 * Utilities for skype components
 *
 * @author aivanov
 */
public class TgUtils {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    public static SendableMessage markdown(String s) {
        return SendableTextMessage.builder().message(s).parseMode(MARKDOWN).build();
    }

    public static SendableMessage html(String s) {
        return SendableTextMessage.builder().message(s).parseMode(HTML).build();
    }

    public static String sanitize(String html) {
        return html.replaceAll("&apos;", "'").replaceAll("<s raw_pre=\"~\" raw_post=\"~\">", "").replaceAll("</s>", "").replaceAll("<ss.*[^(\\/s)]>", "").replaceAll("</ss>", "");
    }

    public static String pre(String text) {
        return "<pre raw_pre=\"{code}\" raw_post=\"{code}\">" + text + "</pre>";
    }

    public static String b(String text) {
        return "<b raw_pre=\"*\" raw_post=\"*\">" + text + "</b>";
    }

    public static String i(String text) {
        return "<i raw_pre=\"_\" raw_post=\"_\">" + text + "</i>";
    }

    public static String prepareQuotes(String s) {
        try {
            String xml = XmlHelper.transformXml("<skypeMessage>" + s + "</skypeMessage>", "xslt/quotes.xslt");
            String prepared = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?><skypeMessage>", "").replaceAll("</skypeMessage>", "");
            return sanitize(prepared);
        } catch (TransformerException e) {
            logger.warning(e.toString());
        }
        return null;
    }

}
