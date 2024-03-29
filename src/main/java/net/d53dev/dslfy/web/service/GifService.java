package net.d53dev.dslfy.web.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;


import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by davidsere on 18/11/15.
 * Adapted from http://riven8192.blogspot.ro/2010/02/image-java-animated-gifs.html
 * under BSD-3 license
 */
@Service
public class GifService {

    public class GifFrame
    {
        public static final String NONE                = "none";
        public static final String DO_NOT_DISPOSE      = "doNotDispose";
        public static final String RESTORE_TO_BGCOLOR  = "restoreToBackgroundColor";
        public static final String RESTORE_TO_PREVIOUS = "restoreToPrevious";

        public final BufferedImage img;
        public final long          delay; // in millis
        public final String        disposalMethod;

        public GifFrame(BufferedImage img, long delay)
        {
            this(img, delay, NONE);
        }

        public GifFrame(BufferedImage img, long delay, String disposalMethod)
        {
            this.img = img;
            this.delay = delay;
            this.disposalMethod = disposalMethod;
        }
    }

    public BufferedImage convertRGBAToGIF(BufferedImage src, int transColor)
    {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
        Graphics g = dst.getGraphics();
        g.setColor(new Color(transColor));
        g.fillRect(0, 0, dst.getWidth(), dst.getHeight());
        {
            IndexColorModel indexedModel = (IndexColorModel) dst.getColorModel();
            WritableRaster raster = dst.getRaster();
            int sample = raster.getSample(0, 0, 0);
            int size = indexedModel.getMapSize();
            byte[] rr = new byte[size];
            byte[] gg = new byte[size];
            byte[] bb = new byte[size];
            indexedModel.getReds(rr);
            indexedModel.getGreens(gg);
            indexedModel.getBlues(bb);
            IndexColorModel newModel = new IndexColorModel(8, size, rr, gg, bb, sample);
            dst = new BufferedImage(newModel, raster, dst.isAlphaPremultiplied(), null);
        }
        dst.createGraphics().drawImage(src, 0, 0, null);
        return dst;
    }

    public void saveAnimatedGIF(OutputStream out, List<GifFrame> frames, int loopCount) throws Exception
    {
        ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        iw.setOutput(ios);
        iw.prepareWriteSequence(null);

        int p = 0;
        for (GifFrame frame : frames)
        {
            ImageWriteParam iwp = iw.getDefaultWriteParam();
            IIOMetadata metadata = iw.getDefaultImageMetadata(new ImageTypeSpecifier(frame.img), iwp);
            this.configureGIFFrame(metadata, String.valueOf(frame.delay / 10L), p++, frame.disposalMethod, loopCount);
            IIOImage ii = new IIOImage(frame.img, null, metadata);
            iw.writeToSequence(ii, null);
        }

        iw.endWriteSequence();
        ios.close();
    }

    private void configureGIFFrame(IIOMetadata meta, String delayTime, int imageIndex, String disposalMethod, int loopCount)
    {
        String metaFormat = meta.getNativeMetadataFormatName();

        if (!"javax_imageio_gif_image_1.0".equals(metaFormat))
        {
            throw new IllegalArgumentException("Unfamiliar gif metadata format: " + metaFormat);
        }

        Node root = meta.getAsTree(metaFormat);

        Node child = root.getFirstChild();
        while (child != null)
        {
            if ("GraphicControlExtension".equals(child.getNodeName()))
                break;
            child = child.getNextSibling();
        }

        IIOMetadataNode gce = (IIOMetadataNode) child;
        gce.setAttribute("userDelay", "FALSE");
        gce.setAttribute("delayTime", delayTime);
        gce.setAttribute("disposalMethod", disposalMethod);

        if (imageIndex == 0)
        {
            IIOMetadataNode aes = new IIOMetadataNode("ApplicationExtensions");
            IIOMetadataNode ae = new IIOMetadataNode("ApplicationExtension");
            ae.setAttribute("applicationID", "NETSCAPE");
            ae.setAttribute("authenticationCode", "2.0");
            byte[] uo = new byte[] { 0x1, (byte) (loopCount & 0xFF), (byte) ((loopCount >> 8) & 0xFF) };
            ae.setUserObject(uo);
            aes.appendChild(ae);
            root.appendChild(aes);
        }

        try
        {
            meta.setFromTree(metaFormat, root);
        }
        catch (IIOInvalidTreeException e)
        {
            throw new Error(e);
        }
    }
}
