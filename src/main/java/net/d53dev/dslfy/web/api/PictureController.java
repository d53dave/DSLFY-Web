package net.d53dev.dslfy.web.api;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.model.DSLFYAnimation;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.service.PictureService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by davidsere on 10/11/15.
 */
@Controller
public class PictureController {

    @Autowired
    private PictureService pictureService;

    private static final Logger LOGGER = Logger.getLogger(PictureController.class);

    @RequestMapping(value = ClientApiV1.PICTURE_GET, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable Long imageId, Principal principal, HttpServletResponse response){
        // TODO: This does not check if a user is allowed to obtain the image
        LOGGER.debug("Getting image for principal "+principal.getName());

        // only a normal token check is performed
        Iterable<DSLFYImage> images = pictureService.getImagesForIdsWithImageData(imageId);

        if(images == null || Iterables.isEmpty(images)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return images.iterator().next().getImageData().getImageData();
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
    public @ResponseBody String handleFileUpload(@PathVariable("userId") String userId,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                DSLFYImage image = new DSLFYImage();

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + "!";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }
}
