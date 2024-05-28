package woowacourse.shopping.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.product.remote.retrofit.ProductResponse
import java.util.concurrent.CompletableFuture

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1,
    ): CompletableFuture<ProductResponse>
}
