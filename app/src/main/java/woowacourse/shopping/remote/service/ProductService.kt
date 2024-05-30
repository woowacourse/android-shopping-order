package woowacourse.shopping.remote.service

import retrofit2.Call
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.remote.RetrofitModule
import woowacourse.shopping.remote.dto.response.ProductResponse
import woowacourse.shopping.remote.dto.response.ProductsResponse

interface ProductService {
    @GET("products")
    fun fetchProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsResponse>

    @GET("products")
    fun fetchProducts(
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ProductsResponse>

    @GET("products/{id}")
    fun fetchDetailProduct(
        @Path("id") id: Long,
    ): Call<ProductResponse>

    companion object {
        private var Instance: ProductService? = null

        fun instance(): ProductService =
            Instance ?: synchronized(this) {
                Instance ?: RetrofitModule.retrofit().create<ProductService>().also {
                    Instance = it
                }
            }
    }
}
