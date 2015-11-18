package net.d53dev.dslfy.web.service;

import java.io.IOException;

/**
 * Created by davidsere on 18/11/15.
 */
public class FFmpegUtil {

    public static void createMorphMp4(String path) throws IOException, InterruptedException {
        Runtime.getRuntime().exec(
                "/usr/local/bin/ffmpeg -y -v error -i "+
                        path+"/%05d.jpg -f mp4 -r 24/1 "+path+"/output.mp4").waitFor();
    }
}
