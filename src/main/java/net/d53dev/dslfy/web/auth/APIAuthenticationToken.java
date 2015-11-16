package net.d53dev.dslfy.web.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by davidsere on 16/11/15.
 */
public class APIAuthenticationToken implements Authentication, CredentialsContainer {

    public static final String API_TOKEN_IDENTIFIER = "API_TOKEN";

    private final String token;
    private final Collection<GrantedAuthority> grantedAuthorities;
    private final String username;

    public APIAuthenticationToken(String username, Collection<GrantedAuthority> grantedAuthorities){
        this.username = username;
        this.token = APITokenUtil.INSTANCE.getToken(username);
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getDetails() {
        return API_TOKEN_IDENTIFIER;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public boolean isAuthenticated() {
        return APITokenUtil.INSTANCE.validateToken(this.token, this.username);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalStateException("Authentication is determined by APITokenUtil");
    }

    @Override
    public boolean equals(Object another) {
        return EqualsBuilder.reflectionEquals(this, another, false);
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public void eraseCredentials() {

    }
}
