package org.formatko.skygram;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.io.File.separator;

/**
 * Main class starter
 * The app have 5 attempts to start if was an error
 *
 * @author aivanov
 */
public class Main {

    private static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    private static int attempt = 5;
    private static String botKey;
    private static String storePath;

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.out.println("Must be 2 args - botApiKey and path to the store.");
                System.exit(1);
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

        if (storePath == null) {
            storePath = args[1];
        }

        if (botKey != null && !botKey.isEmpty()) {
            Skygram skygram;
            try {
                skygram = new Skygram(botKey, storePath);
                skygram.start();
            } catch (Exception e) {
                attempt--;
                logger.log(Level.SEVERE, "Error in application. Attempt left: " + attempt, e);
                if (attempt >= 0) {
                    logger.log(Level.INFO, "Restarting...");
                    try {
                        Thread.sleep(10000);
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
