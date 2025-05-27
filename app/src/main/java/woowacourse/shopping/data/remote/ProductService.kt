package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.dto.product.ProductContent
import woowacourse.shopping.data.dto.product.ProductsResponse

interface ProductService {
    @GET("/products/{id}")
    fun requestProductById(
        @Path(value = "id") id: Long,
    ): Call<ProductContent>

    @GET("/products")
    fun requestProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductsResponse>
}
