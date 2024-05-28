package woowacourse.shopping.data.service

import androidx.room.Delete
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.RequestProductsPostDto
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto

interface ProductService {
    @GET("/products?page={page}&size={size}")
    fun getProductsByOffset(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ResponseProductsGetDto>

    @GET("/products")
    fun getProducts(
        @Header("accept") accept: String = "*/*",
    ): Call<ResponseProductsGetDto>

    @POST("/products")
    fun postProducts(
        @Header("accept") accept: String = "*/*",
        @Body request: RequestProductsPostDto,
    ): Call<RequestProductsPostDto>

    @GET("/products/{id}")
    fun getProductsById(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int,
    ): Call<ResponseProductIdGetDto>

    @Delete
    fun deleteProduct(
        @Header("accept") accept: String = "*/*",
    ): Call<Unit>
}
