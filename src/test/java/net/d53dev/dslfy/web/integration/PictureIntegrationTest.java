package net.d53dev.dslfy.web.integration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import net.d53dev.dslfy.web.DSLFYWebApplication;
import net.d53dev.dslfy.web.repository.ImageDataRepository;
import net.d53dev.dslfy.web.repository.ImageRepository;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by davidsere on 17/11/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DSLFYWebApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class PictureIntegrationTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageDataRepository imageDataRepository;

    @Before
    public void setUp(){
        imageRepository.deleteAll();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.port = port;
    }
}
