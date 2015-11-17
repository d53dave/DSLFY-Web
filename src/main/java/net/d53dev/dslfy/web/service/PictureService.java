package net.d53dev.dslfy.web.service;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.model.DSLFYImageData;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.repository.ImageDataRepository;
import net.d53dev.dslfy.web.repository.ImageRepository;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public Iterable<DSLFYImage> getImagesForIds(Long... ids){
        return imageRepository.findAll(Arrays.asList(ids));
    }

    public Iterable<DSLFYImage> getImagesForIdsWithImageData(Long... ids){
        Iterable<DSLFYImage> images = this.getImagesForIds(ids);
        images.forEach(img -> {
            DSLFYImageData imageData = mapGridFSFile(imageDataRepository.getById(img.getId() + ""));

            img.setImageData(imageData);
        });

        return images;
    }

    private DSLFYImageData mapGridFSFile(GridFSFile gridFSFile){
        if(gridFSFile == null){
            return null;
        }

        DSLFYImageData imageData = new DSLFYImageData();
        byte[] rawData = (byte[]) gridFSFile.get("imageData");
        imageData.setImageData(rawData);

        return imageData;
    }

    public void saveImage(DSLFYImage image){
        this.imageRepository.save(image);
    }

    public void saveImageWithImageData(DSLFYImage image, MultipartFile multipartFile){
        this.saveImage(image);
        this.imageDataRepository.store(image, multipartFile);
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

    private String mapImageToUrl(DSLFYImage image){
        return null;
    }
}
