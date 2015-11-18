package net.d53dev.dslfy.web.api;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.model.DSLFYAnimation;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.model.DSLFYImageData;
import net.d53dev.dslfy.web.service.ImageProcessingService;
import net.d53dev.dslfy.web.service.PictureService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by davidsere on 10/11/15.
 */
@Controller
public class PictureController {

    @Autowired
    private PictureService pictureService;

    @Autowired
    private ImageProcessingService imageProcessingService;

    private static final Logger LOGGER = Logger.getLogger(PictureController.class);

    @RequestMapping(value = ClientApiV1.PICTURE_GET, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    byte[] getImage(@PathVariable Long imageId, Principal principal, HttpServletResponse response){
        // TODO: This does not check if a user is allowed to obtain the image
        LOGGER.debug("Getting image for principal "+principal.getName());

        Iterable<DSLFYImage> images = this.pictureService.getImagesForIdsWithImageData(imageId);

        if(images == null || Iterators.size(images.iterator()) == 0){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return images.iterator().next().getImageData().getImageData();

    }

    @RequestMapping(value = ClientApiV1.apiPrefix+"videotest", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getVideo(Principal principal, HttpServletResponse response){
        LOGGER.debug("Getting video for principal "+principal.getName());

        try {
            LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.put("Content-Type", Lists.newArrayList("video/mp4"));
            byte[] bytes = imageProcessingService.testProcess();
            ResponseEntity<byte[]> responseBytes = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
            return responseBytes;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = ClientApiV1.USER_REQUEST_ANIM, method = RequestMethod.GET)
    public @ResponseBody
    DSLFYAnimation getAnimation(@RequestParam("ids") Long[] ids){

        return null;
    }

    @RequestMapping(value = ClientApiV1.USER_FRIEND_STREAM, method = RequestMethod.GET)
    public @ResponseBody
    Collection<String> getFriendStreamUrls(@PathVariable("userId") Long userId){
        return this.pictureService.getPicturesForUserFriendStream(userId);
    }

    @RequestMapping(value = ClientApiV1.USER_PICTURES, method = RequestMethod.GET)
    public @ResponseBody
    Collection<DSLFYImage> getPictures(@PathVariable Long userId, HttpServletResponse response){

        Collection<DSLFYImage> pics = this.pictureService.getPicturesForUser(userId);

        if(pics == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        return pics;
    }


    @RequestMapping(value = ClientApiV1.USER_UPLOAD, method=RequestMethod.POST)
    public @ResponseBody DSLFYImage handleFileUpload(@PathVariable("userId") Long userId,
                                                    @RequestParam("file") MultipartFile file,
                                                     HttpServletResponse response){
        if (!file.isEmpty()) {
            try {
                DSLFYImage image = new DSLFYImage();
                image.setDescriptor(file.getOriginalFilename());
//                image.setCreateDate();
                image.setUploadDate(LocalDateTime.now());

                pictureService.saveImageWithImageData(image, file);

                image.setImageData(null); //dont write the data to response

                return image;
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return null;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }
}
