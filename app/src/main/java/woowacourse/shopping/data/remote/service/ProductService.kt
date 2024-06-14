package woowacourse.shopping.data.remote.service

import androidx.room.Delete
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.request.RequestProductsPostDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto

interface ProductService {
    @GET("/products")
    suspend fun getProductsByOffset(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ResponseProductsGetDto>

    @GET("/products")
    suspend fun getProducts(): Response<ResponseProductsGetDto>

    @POST("/products")
    suspend fun postProducts(
        @Body request: RequestProductsPostDto,
    ): Response<RequestProductsPostDto>

    @GET("/products/{id}")
    suspend fun getProductsById(
        @Path("id") id: Long,
    ): Response<ResponseProductIdGetDto>

    @Delete
    suspend fun deleteProduct(): Response<Unit>
}
