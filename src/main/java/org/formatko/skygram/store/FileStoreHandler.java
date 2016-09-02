package org.formatko.skygram.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.formatko.skygram.model.Store;

import java.io.*;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * Implementations for store handler/ Use the file to store data
 *
 * @author aivanov
 */
public class FileStoreHandler implements StoreHandler {

    public static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
    private static String FILENAME = "store.json";

    private File file;
    private String filePath;
    private Gson gson;

    public FileStoreHandler() {
        this("");
    }

    public FileStoreHandler(String filePath) {
        this.filePath = filePath;
        logger.info("Path to store=" + this.filePath );
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public synchronized Store load() {
        file = new File(filePath);
        if (file.exists()) {
            logger.info("Store file exist");
            return loadFromFile();
        } else {
            try {
                file.getParentFile().mkdirs();
                boolean newFile = file.createNewFile();
                logger.info("FileStore is created: " + newFile);
            } catch (IOException e) {
                logger.log(SEVERE, "Can't create file store", e);
            }
            Store store = new Store();
            save(store);
            return store;
        }
    }

    @Override
    public synchronized Boolean save(Store store) {
        String json = gson.toJson(store);

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes());
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            logger.log(SEVERE, "The config could not be saved as the file couldn't be found on the storage device.", e);
        } catch (IOException e) {
            logger.log(SEVERE, "The config could not be written to as an error occured. Please check the directories read/write permissions and contact the developer!", e);
        }

        return false;
    }

    private Store loadFromFile() {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
            return gson.fromJson(reader, Store.class);
        } catch (IOException e) {
            logger.log(SEVERE, e.getMessage(), e);
        }

        return null;
    }
}
