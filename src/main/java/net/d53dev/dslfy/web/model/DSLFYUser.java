package net.d53dev.dslfy.web.model;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.*;
import java.util.*;

/**
 * Created by davidsere on 10/11/15.
 */
@Entity
public class DSLFYUser implements Comparable{

    private UserType userType;
    private String username;
    @Transient
    private String activeToken;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany
    @ElementCollection(fetch=FetchType.LAZY)
    private Set<DSLFYImage> userImages;

    @OneToMany
    @ElementCollection(fetch=FetchType.LAZY)
    private Set<DSLFYUser> friends;

    public DSLFYUser(){
    }

    public DSLFYUser(String username) {
        this();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Set<DSLFYUser> getFriends() {
        return friends;
    }

    public void setFriends(Set<DSLFYUser> friends) {
        this.friends = friends;
    }

    public Set<DSLFYImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(Set<DSLFYImage> userImages) {
        this.userImages = userImages;
    }

    @Override
    public int compareTo(Object o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }

    public String getActiveToken() {
        return activeToken;
    }

    public void setActiveToken(String activeToken) {
        this.activeToken = activeToken;
    }
}
