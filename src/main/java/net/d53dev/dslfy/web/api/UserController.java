package net.d53dev.dslfy.web.api;

import com.google.common.base.Strings;
import net.d53dev.dslfy.web.auth.APITokenUtil;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.model.DSLFYImage;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.model.UserType;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Created by davidsere on 10/11/15.
 */
@Controller
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    private DSLFYUser registerUser(DSLFYUser user){
        if(StringUtils.containsIgnoreCase(user.getUsername(), "facebook")){
            user.setUserType(UserType.FACEBOOK);
        } else if (StringUtils.containsIgnoreCase(user.getUsername(), "twitter")){
            user.setUserType(UserType.TWITTER);
        } else {
            throw new IllegalStateException("Username is malformed.");
        }

        DSLFYUser registeredUser = userRepository.save(user);
        return registeredUser;
    }

    private void doLogin(DSLFYUser user){
        user.setActiveToken(APITokenUtil.INSTANCE.getToken(user.getUsername()));
        LOGGER.info("User "+user.getUsername()+" is now logged in.");
        LOGGER.debug("User "+user.getUsername()+" now has token "+user.getActiveToken());
    }

    @RequestMapping(value = ClientApiV1.LOGIN, method = RequestMethod.POST)
    @ResponseBody
    public DSLFYUser loginUser(@RequestBody DSLFYUser user){
        DSLFYUser userFromRepo = userRepository.findByUsername(user.getUsername());

        if(userFromRepo == null){
            LOGGER.info("User "+user.getUsername()+" is new, creating new account.");
            userFromRepo = registerUser(user);
        }

        this.doLogin(userFromRepo);

        return user;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody public String hello(HttpSession session){
        return ReflectionToStringBuilder.toString(session);
    }

    @RequestMapping(value = ClientApiV1.USER_ADD_FRIEND, method = RequestMethod.PUT)
    @ResponseBody DSLFYUser addFriend(@PathVariable Long userId, @PathVariable Long friendId, HttpServletResponse response){
        if(userId.equals(friendId)){
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }

        DSLFYUser user = userRepository.findOne(userId);
        DSLFYUser friend = userRepository.findOne(friendId);

        if(user == null || friend == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        user.getFriends().add(friend);

        return user;
    }

    @RequestMapping(value = ClientApiV1.USER_ADD_FRIENDS, method = RequestMethod.PUT)
    @ResponseBody DSLFYUser addFriends(@PathVariable Long userId, @RequestParam("friendIds[]") Long[] friendIds,
                                       HttpServletResponse response){
        DSLFYUser user = userRepository.findOne(userId);

        if(user == null){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        for(Long friendId:friendIds){
            DSLFYUser friend = userRepository.findOne(friendId);
            if(friend != null){
                user.getFriends().add(friend);
            }
        }

        return user;
    }

}
