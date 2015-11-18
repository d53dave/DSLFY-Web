package net.d53dev.dslfy.web.service;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;
import net.d53dev.dslfy.web.model.DSLFYFilter;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.model.DSLFYImageData;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.repository.ImageDataRepository;
import net.d53dev.dslfy.web.repository.ImageRepository;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by davidsere on 16/11/15.
 */
@Service
public class PictureService {
    private static final Logger LOGGER = Logger.getLogger(PictureService.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageDataRepository imageDataRepository;

    public PictureService(){
//        this.imageRepository.deleteAll();
//        this.clearGridFS();
    }

    public Iterable<DSLFYImage> getImagesForIds(Long... ids){
        return imageRepository.findAll(Arrays.asList(ids));
    }

    public Iterable<DSLFYImage> getImagesForIdsWithImageData(Long... ids){
        Iterable<DSLFYImage> images = this.getImagesForIds(ids);
        images.forEach(img -> {
            DSLFYImageData imageData = mapGridFSFile(imageDataRepository.getById(img.getImageDataId()));

            img.setImageData(imageData);
        });

        return images;
    }

    private DSLFYImageData mapGridFSFile(GridFSDBFile gridFSFile){
        if(gridFSFile == null){
            return null;
        }

        DSLFYImageData imageData = new DSLFYImageData();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            gridFSFile.writeTo(baos);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            return null;
        }
        byte[] rawData = (byte[]) baos.toByteArray();
        imageData.setImageData(rawData);

        return imageData;
    }

    public DSLFYImage saveImage(DSLFYImage image){
        return this.imageRepository.save(image);
    }

    public void saveImageWithImageData(DSLFYImage image, MultipartFile multipartFile){
        String id = this.imageDataRepository.store(image, multipartFile);
        image.setImageDataId(id);
        this.saveImage(image);
    }

    public Collection<DSLFYImage> getPicturesForUser(Long userId){
        DSLFYUser repouser = userRepository.findOne(userId);

        if(repouser == null){
            return null;
        }

        //fetch the lazy collection
        repouser.getUserImages().size();

        return repouser.getUserImages();
    }

    public Collection<String> getPicturesForUserFriendStream(Long userId){
        DSLFYUser repouser = userRepository.findOne(userId);

        if(repouser == null){
            return null;
        }

        Set<DSLFYImage> images = repouser.getUserImages();

        return images
                .stream()
                .sorted( (image1, image2)
                        -> image1.getCreateDate().compareTo(image2.getCreateDate()))
                .limit(50)
                .map( this::mapImageToUrl ).collect(Collectors.toList());

    }

    private void clearGridFS(){
        this.imageDataRepository.clearData();
    }

    private String mapImageToUrl(DSLFYImage image){
        return null;
    }
}
