package xyz.aornice.tofq.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 *
 * Locate the target file by index
 *
 * Created by cat on 2017/4/11.
 */
public class FileLocator{
    private static final int MESSAGES_POW =10;
    // the message count in each file, must be 2^n, because used the fast module method: n & (m-1)
    private static final long MESSAGES_PER_FILE = 2<<MESSAGES_POW;

    private static final int INIT_TOPIC_FILES = 128;

    // date length in file name
    private static final int DATE_LENGTH = 8;

    // file suffix length
    private static final int SUFFIX_LENGTH = 4;

    private static Map<String, ArrayList<String>> topicFileMap = new HashMap<>();

    static{
        for (String topic: TopicCenter.getTopics()){
            ArrayList<String> files = createFileList();

            Path topicFolder = Paths.get(TopicCenter.getPath(topic));
            SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
                    if (attr.isRegularFile()) {
                        String fileName = file.getFileName().toString();
                        files.add(fileName);
                    }
                    return super.visitFile(file, attr);
                }
            };

            try {
                java.nio.file.Files.walkFileTree(topicFolder, Collections.EMPTY_SET, 1, finder);

                // file name format is YYYYMMDD{Number}SUFFIX
                files.sort(new Comparator<String>() {
                    @Override
                    public int compare(String file1, String file2) {
                        // first compare the date
                        for (int i=0;i<DATE_LENGTH; i++) {
                            if (file1.charAt(i) != file2.charAt(i)){
                                return file1.charAt(i)-file2.charAt(i);
                            }
                        }
                        // smaller if in the same date and file name is shorter
                        if (file1.length() != file2.length()){
                            return file1.length() - file2.length();
                        }
                        // compare the number
                        int number1 = Integer.parseInt(file1.substring(DATE_LENGTH, file1.length()-SUFFIX_LENGTH));
                        int number2 = Integer.parseInt(file2.substring(DATE_LENGTH, file2.length()-SUFFIX_LENGTH));

                        return number1-number2;
                    }
                });

                topicFileMap.put(topic, files);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calculate the relative shift of a message in file
     *
     * @param index
     * @return
     */
    public static final long shift(long index){
        return index&(MESSAGES_PER_FILE-1);
    }

    /**
     * Calculate index of the file this message belongs to
     *
     * @param topic
     * @param index  the message index
     * @return       return null if the index is out of current bound
     */
    public static String fileName(String topic, long index){
        int fileInd = fileIndex(index);
        ArrayList<String> files = topicFileMap.get(topic);
        if (fileInd < files.size()) {
            return files.get(fileInd);
        }else{
            return null;
        }
    }

    /**
     * TODO did not consider the case of deleting file
     * When consider deleting, file index can be calculated by minus a shift.
     * When delete file, the filename list in topicFileMap should also be adjusted.
     *
     *
     * @param index
     * @return     return int because java only permits at most Integer.MAX_VALUE elements in ArrayList
     */
    private static final int fileIndex(long index){
        return (int)(index >> MESSAGES_POW);
    }

    private static ArrayList<String> createFileList(){
        return new ArrayList<String>(INIT_TOPIC_FILES);
    }

    /**
     * Should register the new file when file is created
     * Since the writing operation is serial, this method should not be called in parallel.
     *
     * The new created topic will be registered when the first file is added under the topic
     *
     * @param topic
     * @param filename
     */
    public static void registerNewFile(String topic, String filename) {
        ArrayList<String> files = topicFileMap.get(topic);
        if (files == null){
            files = createFileList();
            topicFileMap.put(topic, files);
        }
        // the new file must be the last file
        files.add(filename);
    }

}
