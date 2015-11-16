package net.d53dev.dslfy.web.repository;

import net.d53dev.dslfy.web.model.DSLFYUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by davidsere on 16/11/15.
 */
@Repository
public interface UserRepository extends CrudRepository<DSLFYUser, Long> {
    DSLFYUser findByUsername(String username);

}
