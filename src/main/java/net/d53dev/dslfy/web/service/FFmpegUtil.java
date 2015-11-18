package net.d53dev.dslfy.web.service;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by davidsere on 18/11/15.
 */
@Service
public class FFmpegUtil {


    private static final Logger LOGGER = Logger.getLogger(FFmpegUtil.class);

    public File createMorphMp4(String path) throws IOException, InterruptedException {

        LOGGER.info("Morphing files from path "+path+"/%05d.jpg into "+path+"/output.mp4");

        File outputPath = new File(path);
        File outputTempDir = File.createTempFile("folder-name","");
        outputTempDir.delete();
        outputTempDir.mkdir();

        ProcessBuilder pb =  new ProcessBuilder(
                "/usr/local/bin/ffmpeg",
//                "-y",
//                "-v", "error",
                "-i", "./%05d.jpg",
                "-f", "mp4",
                "-nostdin",
                outputTempDir.getCanonicalPath()+"/output.mp4").inheritIO().directory(outputPath);

        LOGGER.info("Executing "+pb.command().stream().collect(Collectors.joining(" ")));
        Process p = pb.start();

        p.waitFor();
        LOGGER.info("FFmpeg process exited with status "+p.exitValue() +"Filesize: "+new File(outputTempDir.getCanonicalPath()+"/output.mp4").length()/1000+"kb");

        ProcessBuilder pb2 = new ProcessBuilder(
                "/bin/mv",
                outputTempDir.getCanonicalPath()+"/output.mp4",
                path+"/output.mp4"
        ).inheritIO();

        LOGGER.info("Executing "+pb2.command().stream().collect(Collectors.joining(" ")));
        p = pb2.start();

        p.waitFor();
        LOGGER.info("mv process exited with status "+p.exitValue() +"Filesize: "+new File(path+"/output.mp4").length()/1000+"kb");


        return new File(path+"/output.mp4");
    }
}
