package net.d53dev.dslfy.web.config;

/**
 * Created by davidsere on 16/11/15.
 */
public class ConfigConstants {

    public static final String MONGO_DB_NAME = "dslfy-web";
    public static final String MONGO_DB_HOST = "127.0.0.1";

    public static final String EH_CACHE_NAME = "dslfy-cache";
    public static final String EH_CACHE_POLICY = "LRU";
    public static final int    EH_CACHE_MAXSIZE = 512;
    public static final String EH_CACHE_SESSION_PREFIX = "JSESS";
    public static final String EH_CACHE_TOKEN_PREFIX  = "TOKEN";


    public static final String FFMPEG_PATH = "/usr/local/bin/ffmpeg";
    public static final String FFPROBE_PATH = "/usr/local/bin/ffprobe";
}
