package xyz.aornice.tofq.harbour;

import java.util.List;

/**
 * Created by drfish on 10/04/2017.
 */
public interface Harbour {
    /**
     * @param fileName
     * @param offsetFrom start offset in byte, included
     * @param offsetTo   end offset in byte, not included
     * @return
     */
    byte[] get(String fileName, long offsetFrom, long offsetTo);

    List<Long> getLongs(String fileName, long offset, long count);

    long getLong(String fileName, long offset);

    int getInt(String fileName, long offset);

    void put(String fileName, byte[] data, long offset);

    void put(String fileName, long val, long offset);

    void put(String fileName, int val, long offset);

    void flush(String fileName);

    boolean create(String fileName);
}
