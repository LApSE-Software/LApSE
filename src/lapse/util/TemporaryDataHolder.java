/*
 * The MIT License
 *
 * Copyright 2015 Burhanuddin.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lapse.util;

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
