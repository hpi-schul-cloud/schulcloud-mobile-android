package org.schulcloud.mobile.data.remote;

import java.util.List;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.Credentials;
import org.schulcloud.mobile.data.model.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface RestService {

    @GET("users?$limit=-1")
    Observable<List<User>> getUsers();

    @POST("authentication")
    Observable<AccessToken> signIn(@Body Credentials credentials);
}
