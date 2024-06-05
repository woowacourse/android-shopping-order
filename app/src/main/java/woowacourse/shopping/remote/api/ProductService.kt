package woowacourse.shopping.remote.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.remote.model.response.ProductsResponse

interface ProductService {
    @GET(ApiClient.Product.GET_PRODUCTS)
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): ProductsResponse

    @GET(ApiClient.Product.GET_PRODUCTS_BY_ID)
    suspend fun getProductsById(
        @Path("id") id: Int,
    ): ProductResponse
}
