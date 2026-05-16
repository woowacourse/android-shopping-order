package woowacourse.shopping.data.network.product

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.network.cart.dto.ProductDto
import woowacourse.shopping.data.network.product.dto.ProductResponse

interface RetrofitProductService {
    @GET("/products")
    suspend fun requestProducts(
        @Header("accept")
        accept: String = "*/*",
        @Query("category")
        category: String? = null,
        @Query("page")
        page: Int = 0,
        @Query("size")
        size: Int = 1,
    ): Response<ProductResponse>

    @POST("/products")
    suspend fun insertProducts(
        @Header("accept")
        accept: String = "*/*",
        @Body
        product: ProductDto,
    ): Response<Unit>

    @GET("/products/{id}")
    suspend fun getProductDetail(
        @Header("accept")
        accept: String = "*/*",
        @Path("id")
        id: Long = 0,
    ): Response<ProductDto>

    @DELETE("/products/{id}")
    suspend fun deleteProductDetail(
        @Header("accept")
        accept: String = "*/*",
        @Path("id")
        id: Long = 0,
    ): Response<Unit>
}
