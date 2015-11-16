package net.d53dev.dslfy.web.api;

import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.model.DSLFYAnimation;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

/**
 * Created by davidsere on 10/11/15.
 */
@Controller
public class PictureController {

   @Autowired
   private PictureService pictureService;

    @RequestMapping(value = ClientApiV1.USER_REQUEST_ANIM, method = RequestMethod.GET)
    public @ResponseBody
    DSLFYAnimation getAnimation(@RequestParam("ids") Long[] ids){

        return null;
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
