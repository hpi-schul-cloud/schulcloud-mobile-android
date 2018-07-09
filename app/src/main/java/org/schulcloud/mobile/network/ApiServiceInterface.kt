package org.schulcloud.mobile.network

import okhttp3.ResponseBody
import org.schulcloud.mobile.models.AccessToken
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.file.DirectoryResponse
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.devices.Device
import org.schulcloud.mobile.models.topic.Topic
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

    @GET("notification/devices")
    fun getDevices(): Call<FeathersResponse<List<Device>>>
    @GET("notification/devices/{id}")
    fun getDevice(@Path("id") deviceId: String): Call<Device>


}
