package org.schulcloud.mobile.data.remote;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.CallbackRequest;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.DeviceResponse;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
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

    @POST("fileStorage/signedUrl")
    Observable<SignedUrlResponse> generateSignedUrl(@Header("Authorization") String accessToken, @Body SignedUrlRequest signedUrlRequest);

    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @POST("notification/devices")
    Observable<DeviceResponse> createDevice(@Header("Authorization") String accessToken, @Body DeviceRequest deviceRequest);

    @GET("calendar?all=true")
    Observable<List<Event>> getEvents(@Header("Authorization") String accessToken);

    @GET("notification/devices")
    Observable<List<Device>> getDevices(@Header("Authorization") String accessToken);

    @POST("notification/callback")
    Observable<Response<Void>> sendCallback(@Header("Authorization") String accessToken, @Body CallbackRequest callbackRequest);

    @DELETE("notification/devices/{id}")
    Observable<Response<Void>> deleteDevice(@Header("Authorization") String accessToken, @Path("id") String id);

    @GET("homework?$limit=-1&$populate=courseId")
    Observable<List<Homework>> getHomework(@Header("Authorization") String accessToken);
}
