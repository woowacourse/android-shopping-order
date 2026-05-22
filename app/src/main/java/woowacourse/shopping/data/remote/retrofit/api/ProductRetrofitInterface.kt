package woowacourse.shopping.data.remote.retrofit.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.retrofit.dto.ProductDto
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse

interface ProductRetrofitInterface {
    @GET("/products")
    suspend fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>? = null,
        @Query("category") category: String? = null,
    ): ProductResponse

    @GET("/products/{id}")
    suspend fun requestProductDetail(
        @Path("id") id: Long,
    ): ProductDto

    @POST("/products")
    suspend fun addProduct(
        @Body product: ProductDto,
    )

    @DELETE("/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Long,
    )
}
