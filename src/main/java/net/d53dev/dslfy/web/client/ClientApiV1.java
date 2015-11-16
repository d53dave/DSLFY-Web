package net.d53dev.dslfy.web.client;

/**
 * Created by davidsere on 16/11/15.
 */
public class ClientApiV1 {
    public static final String apiPrefix = "/api/v1/";

    public static final String LOGIN =          apiPrefix+"login";

    public static final String USER_UPLOAD =         apiPrefix+"user/{userId}/upload";
    public static final String USER_DELETE =         apiPrefix+"user/{userId}/picture/{pictureId}";
    public static final String USER_LOGOUT =         apiPrefix+"user/{userId}/logout";
    public static final String USER_PICTURES =       apiPrefix+"user/{userId}/pictures";
    public static final String USER_REQUEST_ANIM =   apiPrefix+"user/{userId}/process";
    public static final String USER_ADD_FRIEND  =    apiPrefix+"user/{userId}/friend/{friendId}";
    public static final String USER_ADD_FRIENDS =    apiPrefix+"user/{userId}/friends";
}
