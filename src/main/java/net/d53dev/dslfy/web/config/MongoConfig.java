package net.d53dev.dslfy.web.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
 * Created by davidsere on 16/11/15.
 */
@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Override
    protected String getDatabaseName() {
        return ConfigConstants.MONGO_DB_NAME;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(ConfigConstants.MONGO_DB_HOST);
    }

}
