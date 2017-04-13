package org.schulcloud.mobile.data.remote;

import java.util.List;

import org.schulcloud.mobile.data.model.User;
import retrofit2.http.GET;
import rx.Observable;

public interface RestService {

    @GET("users")
    Observable<List<User>> getUsers();
}
