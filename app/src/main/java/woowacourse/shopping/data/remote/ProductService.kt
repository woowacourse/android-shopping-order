package woowacourse.shopping.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.data.model.product.ProductResponse

interface ProductService {
    @GET("/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): ProductResponse

    @GET("/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int,
    ): Product
}
