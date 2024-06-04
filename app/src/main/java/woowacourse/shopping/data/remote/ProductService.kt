package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse

interface ProductService {
    @GET("/products")
    fun getProducts(
        @Query("category") category: String? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Call<ProductResponse>

    @GET("/products/{id}")
    fun getProductById(
        @Path("id") id: Int,
    ): Call<Product>
}
