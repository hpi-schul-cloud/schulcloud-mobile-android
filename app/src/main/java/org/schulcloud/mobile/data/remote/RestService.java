package org.schulcloud.mobile.data.remote;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RestService {

    @GET("users?$limit=-1")
    Observable<List<User>> getUsers(@Header("Authorization") String accessToken);

    @GET("users/{userId}")
    Observable<CurrentUser> getUser(@Header("Authorization") String accessToken, @Path("userId") String userId);

    @POST("authentication")
    Observable<AccessToken> signIn(@Body Credentials credentials);

    // todo: move Authorization-Header to somewhere better
    @GET("fileStorage")
    Observable<FilesResponse> getFiles(@Header("Authorization") String accessToken, @Query("path") String storageContext);

    @GET("calendar?all=true")
    Observable<List<Event>> getEvents(@Header("Authorization") String accessToken);
}
