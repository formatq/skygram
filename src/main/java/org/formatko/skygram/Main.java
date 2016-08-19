package org.formatko.skygram;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class of org.formatko.skygram
 *
 * @author aivanov
 */
public class Main {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    private static int attempt = 5;
    private static String botKey;

    public static void main(String[] args) {
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
                    main(args);
                } else {
                    logger.log(Level.SEVERE, "Attempts count is end. Stop the application");
                }
            }
        } else {
            logger.log(Level.INFO, "The botKey is not corrected. Stop the application..");
        }
    }
}
