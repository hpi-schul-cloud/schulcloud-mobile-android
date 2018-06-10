package org.schulcloud.mobile.network

import org.schulcloud.mobile.models.AccessToken
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.topic.Topic
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServiceInterface {

    // Login

    @POST("authentication")
    fun createToken(@Body credentials: Credentials): Call<AccessToken>

    // Course

    @GET("courses?\$populate[0]=teacherIds&\$populate[1]=userIds&\$populate[2]=substitutionIds")
    fun listUserCourses(): Call<FeathersResponse<List<Course>>>

    @GET("lessons")
    fun listCourseTopics(@Query("courseId") courseId: String): Call<FeathersResponse<List<Topic>>>

}
