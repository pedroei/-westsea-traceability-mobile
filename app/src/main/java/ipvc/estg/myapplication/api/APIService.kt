package ipvc.estg.myapplication.api

import ipvc.estg.myapplication.models.ActivityDesignation
import ipvc.estg.myapplication.models.CreateActivity
import ipvc.estg.myapplication.models.Product
import ipvc.estg.myapplication.models.Token
import retrofit2.Call
import retrofit2.http.*


interface APIService {
    @FormUrlEncoded
    @POST("/api/login")
    fun login(@FieldMap params: HashMap<String, String>): Call<Token>

    @GET("/api/v1/activity-designation")
    fun getAllActivitiesDesignation(
        @Header("Authorization") authHeader: String?
    ): Call<List<ActivityDesignation>>

    @GET("/api/v1/traceability/activity")
    fun getAllActivities(
        @Header("Authorization") authHeader: String?
    ): Call<List<CreateActivity>>

    @POST("/api/v1/traceability/activity")
    fun createActivity(
        @Header("Authorization") authHeader: String?,
        @Body body: CreateActivity
    ): Call<String>

    @GET("/api/v1/traceability/product")
    fun getAllProducts(
        @Header("Authorization") authHeader: String?
    ): Call<List<Product>>
}

