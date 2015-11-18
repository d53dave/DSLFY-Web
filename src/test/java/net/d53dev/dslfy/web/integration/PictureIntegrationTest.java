package net.d53dev.dslfy.web.integration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import net.d53dev.dslfy.web.DSLFYWebApplication;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.config.ConfigConstants;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.repository.ImageDataRepository;
import net.d53dev.dslfy.web.repository.ImageRepository;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;


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

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp(){
        imageRepository.deleteAll();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.port = port;
    }

    @Test
    public void uploadImageTest() throws IOException{
        final String username = "testuser42@facebook.internal.com";

        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("username", username);

        // Get the token
        DSLFYUser user =
                given()
                        .contentType(ContentType.JSON)
                        .request().body(jsonAsMap)
                    .when()
                        .post(ClientApiV1.apiPrefix+"login")
                    .then()
                        .assertThat().statusCode(200)
                    .extract()
                        .body().as(DSLFYUser.class);

        Resource resource = webApplicationContext.getResource("classpath:dog.jpg");

        DSLFYImage image = given()
                .param("name", "dogjpeglol.jpg")
                .header(ConfigConstants.API_TOKEN_HEADERNAME, user.getActiveToken())
                .multiPart("file", resource.getFile())
            .when()
                .post(ClientApiV1.apiPrefix+"user/"+user.getId()+"/upload")
            .then()
                .assertThat().statusCode(200)
            .extract()
                .body().as(DSLFYImage.class);

        //apiPrefix+"picture/{imageId}";
        byte[] providedImage = given()
                .header(ConfigConstants.API_TOKEN_HEADERNAME, user.getActiveToken())
            .when()
                .get(ClientApiV1.apiPrefix+"picture/"+image.getId())
            .then()
                .assertThat().statusCode(200)
            .extract()
                .body().asByteArray();

        byte[] byteArray = ((DataBufferByte) ImageIO.read(resource.getFile())
                .getData().getDataBuffer()).getData();


        byte[] receivedByteArray = ((DataBufferByte)
                ImageIO.read(new ByteArrayInputStream(providedImage))
                .getData().getDataBuffer()).getData();

        Assert.assertArrayEquals(byteArray, receivedByteArray);
    }
}
