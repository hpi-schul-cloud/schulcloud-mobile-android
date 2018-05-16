package org.schulcloud.mobile.network

import retrofit2.Call
import retrofit2.http.*
import org.schulcloud.mobile.models.AccessToken
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.Course

interface ApiServiceInterface {

    // Login

    @POST("authentication")
    fun createToken(@Body credentials: Credentials): Call<AccessToken>

    // Course

    @GET("courses?\$populate[0]=teacherIds&\$populate[1]=userIds&\$populate[2]=substitutionIds")
    fun listUserCourses(): Call<FeathersResponse<List<Course>>>

}