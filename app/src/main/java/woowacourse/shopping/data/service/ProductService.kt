package woowacourse.shopping.data.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.data.model.response.ProductsResponse

interface ProductService {
    @GET("/products")
    suspend fun getProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Response<ProductsResponse>

    @GET("/products/{id}")
    suspend fun getProduct(
        @Path("id") id: Long,
    ): Response<ProductResponse>
}
