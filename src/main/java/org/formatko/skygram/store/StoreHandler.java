package org.formatko.skygram.store;

import org.formatko.skygram.model.Store;

/**
 * Interface of store handler
 *
 * @author aivanov
 */
public interface StoreHandler {

    Store load();

    Boolean save(Store store);
}
