package net.d53dev.dslfy.web.integration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.Config;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import net.d53dev.dslfy.web.DSLFYWebApplication;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.config.ConfigConstants;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.model.UserType;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;


/**
 * Created by davidsere on 17/11/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DSLFYWebApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class UserIntegrationTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp(){
        userRepository.deleteAll();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.port = port;
    }


    @Test
    public void apiTokenTest(){
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
                .body("activeToken",    is(notNullValue()))
            .extract()
                .body().as(DSLFYUser.class);

        given()
                .content(ContentType.JSON)
                .header(ConfigConstants.API_TOKEN_HEADERNAME, user.getActiveToken())
            .when()
                .get(ClientApiV1.apiPrefix+"user/"+user.getId())
            .then()
                .assertThat().statusCode(200);
    }

    @Test
    public void createUserTest(){
        final String username = "testuser42@facebook.internal.com";

        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("username", username);

        String response = given()
                .contentType(ContentType.JSON)
                .request().body(jsonAsMap)

        .when()
                .post(ClientApiV1.apiPrefix+"login")
        .then()
                .assertThat().statusCode(200)
                .body("username",       equalTo(username))
                .body("userType",       equalTo(UserType.FACEBOOK.toString()))
                .body("id",             is(notNullValue()))
                .body("activeToken",    is(notNullValue()))
        .extract()
                .asString();


        System.out.printf("RESPONSE: "+response);
        Assert.assertNotNull(userRepository.findByUsername(username));
    }

    @Test
    public void addFriendTest() throws UnsupportedEncodingException{
        final String username = "testuser1@facebook.internal.com";
        final String username2 = "testuser2@facebook.internal.com";

        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("username", username);

        DSLFYUser user1 = given()
                .contentType(ContentType.JSON)
                .request().body(jsonAsMap)
        .when()
                .post(ClientApiV1.apiPrefix+"login")
        .then()
                .assertThat().statusCode(200)
        .extract()
                .body().as(DSLFYUser.class);

        jsonAsMap = new HashMap<>();
        jsonAsMap.put("username", username2);

        DSLFYUser user2 = given()
                .contentType(ContentType.JSON)
                .request().body(jsonAsMap)

        .when()
                .post(ClientApiV1.apiPrefix+"login")
        .then()
                .assertThat().statusCode(200)
        .extract()
                .body().as(DSLFYUser.class);

        Assert.assertNotEquals(user1, user2);

        user1 =
            given()
                .content(ContentType.JSON)
                .header(ConfigConstants.API_TOKEN_HEADERNAME, user1.getActiveToken())
            .when()
                .put(ClientApiV1.apiPrefix+"user/"+user1.getId()+"/friend/"+
                        Base64.getEncoder().encodeToString(user2.getUsername().getBytes("UTF-8")))
            .then()
                .assertThat().statusCode(200)
                .body("friends", hasSize(greaterThan(0)))
            .extract()
                .body().as(DSLFYUser.class);

        Assert.assertEquals(user1.getFriends().iterator().next().getId(), user2.getId());
    }
}
