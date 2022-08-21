package ipvc.estg.myapplication.api

import ipvc.estg.myapplication.models.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
        @Body body: RequestBody
    ): Call<String>

    @POST("/api/v1/traceability/product")
    fun createProduct(
        @Header("Authorization") authHeader: String?,
        @Body body: RequestBody
    ): Call<String>

    @GET("/api/v1/traceability/product")
    fun getAllProducts(
        @Header("Authorization") authHeader: String?
    ): Call<List<Product>>

    @GET("/api/v1/traceability/product/{productLotUuid}/document/{documentKey}/download")
    fun downloadDocument(
        @Header("Authorization") authHeader: String?,
        @Path("productLotUuid") productLotUuid: String,
        @Path("documentKey") documentKey: String,
    ): Call<ResponseBody>
}

