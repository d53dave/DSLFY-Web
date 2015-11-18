package net.d53dev.dslfy.web.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by davidsere on 18/11/15.
 */
@Service
public class ImageMagickUtil {

    public void mogrify(String path) throws IOException, InterruptedException {

        Runtime.getRuntime().exec("/usr/local/bin/convert "+path+"/*.jpg -delay 100 -morph 50 "+path+"/%05d.jpg").waitFor();
//
    }

}
