package woowacourse.shopping.data.product.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.product.dto.ProductResponse
import woowacourse.shopping.data.product.dto.ProductsResponse

interface ProductService {
    @GET("/products")
    fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsResponse>

    @GET("/products/{id}")
    fun getProductById(
        @Path("id") id: Long,
    ): Call<ProductResponse>
}
