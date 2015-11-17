package net.d53dev.dslfy.web.auth;

import net.d53dev.dslfy.web.config.ConfigConstants;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Created by davidsere on 16/11/15.
 */
@Component
public class EHCacheSecurityContextRepository implements SecurityContextRepository {


    private final Cache cache = CacheManager.getInstance().getCache(ConfigConstants.EH_CACHE_NAME);

    private static final Logger LOGGER = Logger.getLogger(EHCacheSecurityContextRepository.class);

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest req = requestResponseHolder.getRequest();
        SecurityContext context = getContextFromCache(req);
//        LOGGER.info("ROLES: "+context.getAuthentication().getAuthorities());
        if(context == null){
            context = SecurityContextHolder.createEmptyContext();
        }
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = context.getAuthentication();
        if(authentication != null) {
            if (authentication.getDetails().equals(ConfigConstants.API_TOKEN_IDENTIFIER)) {
                LOGGER.info("Saving context for token "+authentication.getCredentials());
                String token = authentication.getCredentials().toString();
                cache.put(new Element(ConfigConstants.EH_CACHE_TOKEN_PREFIX+token, context));
            } else {
                HttpSession session = request.getSession();
                LOGGER.info("Saving context for session "+session.getId());
                cache.put(new Element(ConfigConstants.EH_CACHE_SESSION_PREFIX+session.getId(), context));
            }
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String token = getTokenFromHeader(request);

        if(token != null){
            return cache.get(ConfigConstants.EH_CACHE_TOKEN_PREFIX+token) != null;
        } else {
            return cache.get(ConfigConstants.EH_CACHE_SESSION_PREFIX
                    +request.getRequestedSessionId()) != null;
        }
    }

    private SecurityContext getContextFromCache(HttpServletRequest request){
        SecurityContext context = null;
        String tokenFromHeader = getTokenFromHeader(request);
        if(tokenFromHeader == null){

            if(request.isRequestedSessionIdValid()){
                String sessId = request.getRequestedSessionId();
                Element e = cache.get(ConfigConstants.EH_CACHE_SESSION_PREFIX+sessId);
                if(e != null){
                    context = (SecurityContext) e.getObjectValue();
                }

                LOGGER.info("Loading context for session "+sessId);

            } else {
                try{
                    request.getSession().invalidate();
                    cache.remove(ConfigConstants.EH_CACHE_SESSION_PREFIX+request.getSession().getId());
                } catch(IllegalStateException | NullPointerException e){
                    //just ignore, session is is new or expired, so no way of getting the id.
                    //ehcache will clean up at some point...
                }
                return null;
            }
        } else {
            Element e = cache.get(ConfigConstants.EH_CACHE_TOKEN_PREFIX+tokenFromHeader);
            if(e != null) {
                context = (SecurityContext)e.getObjectValue();

                if(!context.getAuthentication().isAuthenticated()){
                    cache.remove(ConfigConstants.EH_CACHE_TOKEN_PREFIX+getTokenFromHeader(request));
                    return null;
                }
            }
            LOGGER.info("Loading context for token "+tokenFromHeader);
            //if invalid, we can throw it away...

        }
        return context;
    }

    private String getTokenFromHeader(HttpServletRequest request){
        String token = request.getHeader(ConfigConstants.API_TOKEN_HEADERNAME);
        if(StringUtils.isBlank(token)){
            token = null;
        }
        return token;
    }
}

