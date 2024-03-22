package com.example.ratatouille23desktopclient.helpers;

import java.util.logging.*;

public class CustomLogger {
    private static Logger logger;

    static {
        logger = Logger.getLogger(CustomLogger.class.getName());
        FileHandler fh;

        try {
            // Create a FileHandler
            fh = new FileHandler("RAT23Desktop.log");
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // Set the FileHandler for the logger
            logger.addHandler(fh);

            // Disable the parent handlers to avoid duplicate logs
            logger.setUseParentHandlers(false);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while initializing logger", e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
