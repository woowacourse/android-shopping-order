package woowacourse.shopping.remote.service

import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.response.ProductResponse
import woowacourse.shopping.remote.dto.response.ProductsResponse

interface ProductService {
    @GET("products")
    suspend fun fetchProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductsResponse

    @GET("products")
    suspend fun fetchProducts(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun fetchDetailProduct(
        @Path("id") id: Long,
    ): ProductResponse

    companion object {
        private var instance: ProductService? = null

        fun instance(): ProductService =
            instance ?: synchronized(this) {
                instance ?: RetrofitModule.retrofit().create<ProductService>().also {
                    instance = it
                }
            }
    }
}
