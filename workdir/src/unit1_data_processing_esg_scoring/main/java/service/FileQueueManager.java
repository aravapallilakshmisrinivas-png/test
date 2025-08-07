package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileQueueManager {
    private final ConcurrentLinkedQueue<String> fileQueue = new ConcurrentLinkedQueue<>();

    public void addFileToQueue(String filePath) {
        if (!fileQueue.contains(filePath)) {
            fileQueue.offer(filePath);
            System.out.println("Added file to queue: " + filePath);
        }
    }

    public String getNextFile() {
        return fileQueue.poll();
    }

    public List<String> getQueuedFiles() {
        return new ArrayList<>(fileQueue);
    }

    public boolean hasFiles() {
        return !fileQueue.isEmpty();
    }

    public int getQueueSize() {
        return fileQueue.size();
    }

    public void clearQueue() {
        fileQueue.clear();
    }
}