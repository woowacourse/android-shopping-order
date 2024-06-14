package woowacourse.shopping.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.remote.model.response.ProductsResponse

interface ProductService {
    @GET(PRODUCT_RELATIVE_URL)
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ProductsResponse

    @GET("${PRODUCT_RELATIVE_URL}/{id}")
    suspend fun getProductsById(
        @Path("id") id: Int,
    ): ProductResponse

    companion object {
        private const val PRODUCT_RELATIVE_URL = "/products"
    }
}
