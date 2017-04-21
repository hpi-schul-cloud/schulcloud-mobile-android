package org.schulcloud.mobile.data.remote;

import java.util.List;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface RestService {

    @GET("users?$limit=-1")
    Observable<List<User>> getUsers();

    @POST("authentication")
    Observable<AccessToken> signIn(@Body Credentials credentials);

    // todo: move Authorization-Header to somewhere better
    @GET("fileStorage?storageContext={storageContext}")
    Observable<FilesResponse> getFiles(@Header("Authorization") String accessToken, @Query("storageContext") String storageContext);
}
