package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.dto.product.ProductsResponse
import woowacourse.shopping.domain.model.Product

interface ProductService {
    fun fetchProductById(id: Long): Product

    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductsResponse>

    fun fetchProducts(): List<Product>
}
