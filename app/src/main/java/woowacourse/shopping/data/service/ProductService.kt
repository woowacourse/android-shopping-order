package woowacourse.shopping.data.service

import androidx.room.Delete
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.request.RequestProductsPostDto
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto

interface ProductService {
    @GET("/products")
    fun getProductsByOffset(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ResponseProductsGetDto>

    @GET("/products")
    fun getProductsByCategory(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
    ): Call<ResponseProductsGetDto>

    @POST("/products")
    fun postProducts(
        @Body request: RequestProductsPostDto,
    ): Call<RequestProductsPostDto>

    @GET("/products/{id}")
    fun getProductsById(
        @Path("id") id: Long,
    ): Call<ResponseProductIdGetDto>

    @Delete
    fun deleteProduct(): Call<Unit>
}
