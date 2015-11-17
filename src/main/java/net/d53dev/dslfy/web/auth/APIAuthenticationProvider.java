package net.d53dev.dslfy.web.auth;

import net.d53dev.dslfy.web.config.ConfigConstants;
import net.d53dev.dslfy.web.model.DSLFYUser;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by davidsere on 17/11/15.
 */
@Component
public class APIAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = Logger.getLogger(APIAuthenticationProvider.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object principal = authentication.getPrincipal();
        if(principal != null && !principal.toString().isEmpty()){
            DSLFYUser user = userRepository.findByUsername(authentication.getPrincipal().toString());
            if(user != null){
                Authentication auth;

                if(authentication.getDetails().equals(ConfigConstants.API_TOKEN_IDENTIFIER)){
                    LOGGER.info("Authenticated [API] user "+principal);
                    auth = new APIAuthenticationToken(user.getUsername(), AuthorityUtils.NO_AUTHORITIES);
                } else {
                    LOGGER.info("Authenticating [WEB] user "+principal);
                    auth = new UsernamePasswordAuthenticationToken(user.getUsername(), "", AuthorityUtils.NO_AUTHORITIES);
                }
                SecurityContextHolder.getContext().setAuthentication(auth);
                return SecurityContextHolder.getContext().getAuthentication();
            }
        }


        throw new AuthenticationServiceException(String.format("The username [%s] could not be authenticated.", authentication.getName()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
