package org.formatko.skygram.store;

import org.formatko.skygram.model.Store;

/**
 * Class of org.formatko.skygram.store
 *
 * @author aivanov
 */
public interface StoreHandler {

    Store load();

    Boolean save(Store store);
}
