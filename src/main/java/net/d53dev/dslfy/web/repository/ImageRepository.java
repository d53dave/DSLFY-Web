package net.d53dev.dslfy.web.repository;

import net.d53dev.dslfy.web.model.DSLFYImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by davidsere on 16/11/15.
 */
@Repository
public interface ImageRepository extends CrudRepository<DSLFYImage, Long> {

    DSLFYImage findByDescriptor(String imageDescriptor);
}
