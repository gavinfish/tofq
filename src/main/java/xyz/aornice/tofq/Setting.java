package xyz.aornice.tofq;

/**
 * Created by robin on 18/04/2017.
 */
public class Setting {

    public static final String DEFAULT_BASE_PATH = "/var/tofq/";
    public static final int DEFAULT_BATCH_DEPOSITION_SIZE = 300;
    public static final long DEFAULT_DEPOSITION_INTERVAL_NANO = 5_000_000;

    public static String BASE_PATH = DEFAULT_BASE_PATH;
    public static int BATCH_DEPOSITION_SIZE = DEFAULT_BATCH_DEPOSITION_SIZE;
    public static long DEPOSITION_INTERVAL_NANO = DEFAULT_DEPOSITION_INTERVAL_NANO;
}