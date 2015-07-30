/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawingtagger.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Burhanuddin
 */
public class TemporaryDataHolder {
    private final ObservableMap<String, ObservableList<String>> temp;
    
    /**
     * Constructor.
     */
    public TemporaryDataHolder() {
        temp = FXCollections.observableHashMap();
    }
    
    /**
     * Insert temporary data with associated key.
     * @param key
     * @param data 
     */
    public void insertTempData(String key, String data) {
        if (!temp.containsKey(key)) {
            temp.put(key, FXCollections.observableArrayList());
        }
        temp.get(key).add(data);
    }
    
    /**
     * Retrieve temporary data with associated key.
     * @param key
     * @return 
     */
    public ObservableList<String> retrieveTempData(String key) {
        return temp.get(key);
    }
    
    /**
     * Clear specified temporary data.
     * @param key 
     */
    public void clearTempData(String key) {
        temp.get(key).clear();
    }
    
    /**
     * Clear all temporary data.
     */
    public void clearAll() {
        temp.clear();
    }
    
    /**
     * Return whether the specified key is empty.
     * @param key
     * @return 
     */
    public boolean isEmpty(String key) {
        if (temp.containsKey(key)) {
            return temp.get(key).isEmpty();
        }
        return true;
    }
}
