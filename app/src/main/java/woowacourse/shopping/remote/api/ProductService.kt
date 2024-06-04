package woowacourse.shopping.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.remote.model.response.ProductsResponse

interface ProductService {
    @GET(PRODUCT_BASE_URL)
    fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<ProductsResponse>

    @GET("${PRODUCT_BASE_URL}/{id}")
    fun getProductsById(
        @Path("id") id: Int,
    ): Call<ProductResponse>

    companion object {
        private const val PRODUCT_BASE_URL = "/products"
    }
}
