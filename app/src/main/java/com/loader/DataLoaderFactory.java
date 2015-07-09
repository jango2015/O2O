/**
 * 
 */
package com.loader;


import android.content.Context;

/**
 * @author Acher
 *
 */
public abstract class DataLoaderFactory {
    
    protected Context context;
    
    public DataLoaderFactory(Context context) {
        this.context = context;
    }
    
    public abstract DataLoader getDataLoader(boolean defaultLoader);
    
    public abstract DataLoader getDataLoader(boolean defaultLoader, DataCache.DataCacheParams cacheParams, DataLoaderParams loaderParams);
    
}
