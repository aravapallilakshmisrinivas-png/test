package repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class InMemoryDataStore<K, V> {
    private final ConcurrentHashMap<K, V> dataStore = new ConcurrentHashMap<>();
    
    public void put(K key, V value) {
        dataStore.put(key, value);
    }
    
    public V get(K key) {
        return dataStore.get(key);
    }
    
    public boolean containsKey(K key) {
        return dataStore.containsKey(key);
    }
    
    public V remove(K key) {
        return dataStore.remove(key);
    }
    
    public List<V> getAllValues() {
        return new ArrayList<>(dataStore.values());
    }
    
    public List<K> getAllKeys() {
        return new ArrayList<>(dataStore.keySet());
    }
    
    public void clear() {
        dataStore.clear();
    }
    
    public int size() {
        return dataStore.size();
    }
    
    public boolean isEmpty() {
        return dataStore.isEmpty();
    }
    
    public Map<K, V> getAll() {
        return new ConcurrentHashMap<>(dataStore);
    }
}