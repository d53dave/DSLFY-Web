package net.d53dev.dslfy.web.service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.d53dev.dslfy.web.config.ConfigConstants;
import org.apache.commons.lang3.math.Fraction;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by davidsere on 16/11/15.
 */
@Service
public class ImageProcessingService {
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;

    public ImageProcessingService() throws IOException {
        this.ffmpeg = new FFmpeg(ConfigConstants.FFMPEG_PATH);
        this.ffprobe = new FFprobe(ConfigConstants.FFPROBE_PATH);
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
