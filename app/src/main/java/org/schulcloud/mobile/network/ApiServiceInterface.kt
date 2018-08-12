package org.schulcloud.mobile.network

import com.google.gson.JsonArray
import okhttp3.ResponseBody
import org.json.JSONArray
import org.schulcloud.mobile.models.AccessToken
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.file.DirectoryResponse
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.notifications.Device
import org.schulcloud.mobile.models.notifications.DeviceRequest
import org.schulcloud.mobile.models.notifications.CallbackRequest
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import retrofit2.Call
import retrofit2.http.*


interface ApiServiceInterface {

    // Login
    @POST("authentication")
    fun createToken(@Body credentials: Credentials): Call<AccessToken>

    // Events
    @GET("calendar?all=true")
    fun listEvents(): Call<List<Event>>

    //News
    @GET("news?\$sort=createdAt:1")
    fun listUserNews(): Call<FeathersResponse<List<News>>>

    // Course
    @GET("courses?\$populate[0]=teacherIds")
    fun listUserCourses(): Call<FeathersResponse<List<Course>>>
    @GET("courses/{id}?\$populate[0]=teacherIds")
    fun getCourse(@Path("id") courseId: String): Call<Course>

    @GET("lessons")
    fun listCourseTopics(@Query("courseId") courseId: String): Call<FeathersResponse<List<Topic>>>
    @GET("lessons/{id}")
    fun getTopic(@Path("id") topicId: String): Call<Topic>

    // Homework
    @GET("homework?\$populate=courseId&\$sort=dueDate:-1")
    fun listUserHomework(): Call <FeathersResponse<List<Homework>>>
    @GET("homework/{id}?\$populate=courseId&\$sort=dueDate:-1")
    fun getHomework(@Path("id") homeworkId: String): Call<Homework>

    // File
    @GET("fileStorage")
    fun listDirectoryContents(@Query("path") path: String): Call<DirectoryResponse>
    @POST("fileStorage/signedUrl")
    fun generateSignedUrl(@Body signedUrlRequest: SignedUrlRequest): Call<SignedUrlResponse>
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    //Devices
    @GET("notification/devices")
    fun getDevices(): Call<FeathersResponse<List<Device>>>
    @GET("notification/devices/{id}")
    fun getDevice(@Path("id") deviceId: String): Call<Device>
    @POST("notification/devices")
    fun createDevice(@Body device: DeviceRequest): Call<Void>
    @DELETE("notifcation/devices/{id}")
    fun deleteDevice(@Path("id") deviceId: String): Call<String>
    @POST("notifcation/callback")
    fun sendCallback(@Body callback: CallbackRequest): Call<Void>

    //User Settings
    @GET("users/{id}")
    fun getUser(@Path("id") userId: String): Call <User>
    @PATCH("users/{id}")
    fun patchUser(@Path("id") userId: String, @Body user: User): Call<User>
    @PATCH("accounts/{id}")
    fun patchAccount(@Path("id") accountId: String, @Body account: Account): Call<Account>
    @GET("accounts/")
    fun getAccountForUser(@Query("userId") userId: String): Call<JsonArray>

}
