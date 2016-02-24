
package com.example.z.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RequestModel {
    private Map<String, Object> mData = new HashMap<String, Object>();

    private Map<String, File> mDataFile = new HashMap<String, File>();

    private boolean isNeedCache = true;

    private boolean isCookie = false;

    public boolean isCookie() {
        return isCookie;
    }

    public void setCookie(boolean isCookie) {
        this.isCookie = isCookie;
    }

    public RequestModel(Map<String, Object> mData) {
        super();
        this.mData = mData;
    }

    public RequestModel() {
        super();
    }

    public Map<String, File> getDataFile() {
        return mDataFile;
    }

    public void setDataFile(Map<String, File> mDataFile) {
        this.mDataFile = mDataFile;
    }

    public Map<String, Object> getData() {
        return mData;
    }

    public void setData(Map<String, Object> mData) {
        this.mData = mData;
    }

    public boolean isNeedCache() {
        return isNeedCache;
    }

    public void setNeedCache(boolean isNeedCache) {
        this.isNeedCache = isNeedCache;
    }

    public void put(String key, Object value) {
        mData.put(key, value);
    }

    public void putFile(String key, File file) {
        mDataFile.put(key, file);
    }

}
