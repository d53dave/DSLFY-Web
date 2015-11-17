package net.d53dev.dslfy.web.api;

import com.google.common.base.Strings;
import net.d53dev.dslfy.web.auth.APIAuthenticationToken;
import net.d53dev.dslfy.web.auth.APITokenUtil;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.config.ConfigConstants;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.model.UserType;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;


/**
 * Created by davidsere on 10/11/15.
 */
@Controller
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SecurityContextRepository securityContextRepository;

    private DSLFYUser registerUser(DSLFYUser user){
        if(StringUtils.containsIgnoreCase(user.getUsername(), "facebook")){
            user.setUserType(UserType.FACEBOOK);
        } else if (StringUtils.containsIgnoreCase(user.getUsername(), "twitter")){
            user.setUserType(UserType.TWITTER);
        } else {
            throw new IllegalStateException("Username is malformed.");
        }

        LOGGER.info("User "+user.getUsername()+" is now registered.");
        DSLFYUser registeredUser = userRepository.save(user);
        return registeredUser;
    }

    private void doLogin(DSLFYUser user, HttpServletRequest request,
                         HttpServletResponse response){

        String token = null;
        UsernamePasswordAuthenticationToken authenticationRequest =
                new UsernamePasswordAuthenticationToken(user.getUsername(), "");

        authenticationRequest.setDetails(ConfigConstants.API_TOKEN_IDENTIFIER);

        try{
            APIAuthenticationToken res = (APIAuthenticationToken)
                    authenticationManager.authenticate(authenticationRequest);
            LOGGER.info(ToStringBuilder.reflectionToString(res));
            if(res != null){
                token = res.getCredentials().toString();
                LOGGER.info("Generated token "+token);
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(res);
                this.securityContextRepository.saveContext(context, request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch(AuthenticationException e){
            LOGGER.info("Authentication error: "+e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        user.setActiveToken(token);

        LOGGER.info("User "+user.getUsername()+" is now logged in.");
        LOGGER.debug("User "+user.getUsername()+" now has token "+user.getActiveToken());
    }

    @RequestMapping(value = ClientApiV1.LOGIN, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DSLFYUser loginUser(@RequestBody DSLFYUser user, HttpServletRequest request, HttpServletResponse response){
        DSLFYUser userFromRepo = userRepository.findByUsername(user.getUsername());

        if(userFromRepo == null){
            LOGGER.info("User "+user.getUsername()+" is new, creating new account.");
            userFromRepo = registerUser(user);
        }

        LOGGER.info("Userrepo now holds "+userRepository.count());

        this.doLogin(userFromRepo, request, response);

        return user;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public String hello(HttpSession session){
        return ReflectionToStringBuilder.toString(session);
    }

    @RequestMapping(value = ClientApiV1.USER_ADD_FRIEND, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody DSLFYUser addFriend(@PathVariable Long userId, @PathVariable String friendName, HttpServletResponse response) throws UnsupportedEncodingException {

        String username = new String(Base64.getDecoder().decode(friendName), "UTF-8");
        DSLFYUser friend = userRepository.findByUsername(username);

        if(friend == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        if(userId.equals(friend.getId())){
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }

        DSLFYUser user = userRepository.findOne(userId);

        if(user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        user.getFriends().add(friend);

        return user;
    }

    @RequestMapping(value = ClientApiV1.USER_ADD_FRIENDS, method = RequestMethod.PUT)
    @ResponseBody DSLFYUser addFriends(@PathVariable Long userId, @RequestParam("friendNames[]") String[] friendNames,
                                       HttpServletResponse response){
        DSLFYUser user = userRepository.findOne(userId);

        if(user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        for(String friendName:friendNames){
            DSLFYUser friend = userRepository.findByUsername(friendName);
            if(friend != null){
                user.getFriends().add(friend);
            }
        }

        return user;
    }

    @RequestMapping(value = ClientApiV1.USER_PROFILE,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public DSLFYUser userProfile(@PathVariable Long userId){

        return this.userRepository.findOne(userId);
    }


}
