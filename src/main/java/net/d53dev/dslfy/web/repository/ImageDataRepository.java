package net.d53dev.dslfy.web.repository;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import net.d53dev.dslfy.web.model.DSLFYImage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by davidsere on 16/11/15.
 */
@Repository
public class ImageDataRepository {

    private static final Logger LOGGER = Logger.getLogger(ImageDataRepository.class);

    @Autowired
    private GridFsTemplate gridFsTemplate;


    public String store(InputStream inputStream, String fileName,
                        String contentType, DBObject metaData) {
        return this.gridFsTemplate
                .store(inputStream, fileName, contentType, metaData).getId()
                .toString();
    }

    public String store(DSLFYImage image, MultipartFile multipartFile){
        try{
            return this.gridFsTemplate.store(multipartFile.getInputStream(),
                    image.getDescriptor(), multipartFile.getContentType()).getId().toString();
        } catch (final IOException ioe){
            LOGGER.error("Could not store image: "+ioe.getLocalizedMessage());
            return null;
        }
    }

    public GridFSDBFile getById(String id) {
        return this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(
                id)));
    }

    public GridFSDBFile getByFilename(String fileName) {
        return gridFsTemplate.findOne(new Query(Criteria.where("filename").is(
                fileName)));
    }

    public GridFSDBFile retrive(String fileName) {
        return gridFsTemplate.findOne(
                new Query(Criteria.where("filename").is(fileName)));
    }

    public List findAll() {
        return gridFsTemplate.find(null);
    }
}
