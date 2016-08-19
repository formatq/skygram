package org.formatko.skygram;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.io.File.separator;

/**
 * Class of org.formatko.skygram
 *
 * @author aivanov
 */
public class Main {

    public static String SKYGRAM_PATH = System.getProperty("user.home") + separator + "AppData" + separator + "Roaming" + separator + "Skygram" + separator;

    private static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    private static int attempt = 5;
    private static String botKey;

    public static void main(String[] args) {
        try {
            File homeDir = new File(SKYGRAM_PATH);
            if (!homeDir.exists()) {
                homeDir.mkdirs();
            }
            LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"));
            worker(args);
        } catch (IOException e) {
            logger.severe("Logger problem... " + e.getMessage());
        }
    }

    private static void worker(String[] args) {
        if (botKey == null) {
            botKey = args[0];
        }

        if (botKey != null && !botKey.isEmpty()) {
            Skygram skygram;
            try {
                skygram = new Skygram(botKey);
                skygram.start();
            } catch (Exception e) {
                attempt--;
                logger.log(Level.SEVERE, "Error in application. Attempt left: " + attempt, e);
                if (attempt >= 0) {
                    logger.log(Level.INFO, "Restarting...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    worker(args);
                } else {
                    logger.log(Level.SEVERE, "Attempts count is end. Stop the application");
                }
            }
        } else {
            logger.log(Level.INFO, "The botKey is not corrected. Stop the application..");
        }
    }
}
