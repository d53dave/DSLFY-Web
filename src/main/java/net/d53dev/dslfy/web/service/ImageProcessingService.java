package net.d53dev.dslfy.web.service;

import com.google.common.io.Files;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.d53dev.dslfy.web.config.ConfigConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;

/**
 * Created by davidsere on 16/11/15.
 */
@Service
public class ImageProcessingService {
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;

    @Autowired
    private WebApplicationContext webApplicationContext;

    public ImageProcessingService() throws IOException {
        this.ffmpeg = new FFmpeg(ConfigConstants.FFMPEG_PATH);
        this.ffprobe = new FFprobe(ConfigConstants.FFPROBE_PATH);
    }

    public byte[] testProcess() throws IOException, InterruptedException {
        Resource resource1 = webApplicationContext.getResource("classpath:dog1.jpg");
        Resource resource2 = webApplicationContext.getResource("classpath:dog2.jpg");
        Resource resource3 = webApplicationContext.getResource("classpath:dog3.jpg");
        Resource resource4 = webApplicationContext.getResource("classpath:dog4.jpg");

        File tempdir = File.createTempFile("folder-name","");
        tempdir.delete();
        tempdir.mkdir();

        int i = 0;
        for(Resource r:  new Resource[]{resource1, resource2, resource3, resource4}){
            String name = String.format("%s.%s", (i++)+RandomStringUtils.randomAlphanumeric(10), "jpg");
            File newFile = new File(tempdir.getCanonicalPath()+"/"+name);
            FileUtils.copyFile(r.getFile(), newFile);
        }

        ImageMagickUtil imageMagickUtil = new ImageMagickUtil();
        imageMagickUtil.mogrify(tempdir.getCanonicalPath());

        FFmpegUtil.createMorphMp4(tempdir.getCanonicalPath());

        return IOUtils.toByteArray(new FileInputStream(tempdir.getCanonicalPath()+"/output.mp4"));
    }

    public void doSomeThings(String file){
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(file)
                .overrideOutputFiles(true)
                .addOutput("output.mp4")
                .setFormat("mp4")
                .setTargetSize(250000)

                .disableSubtitle()

                .setAudioChannels(1)
                .setAudioCodec("libfdk_aac")
                .setAudioSampleRate(48000)
                .setAudioBitRate(32768)

                .setVideoCodec("libx264")
                .setVideoFrameRate(Fraction.getFraction(24, 1))
                .setVideoResolution(640, 480)

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createTwoPassJob(builder).run();
    }
}
