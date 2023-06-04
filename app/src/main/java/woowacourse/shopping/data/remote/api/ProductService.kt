package woowacourse.shopping.data.remote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import woowacourse.shopping.data.remote.response.ProductResponseDTO

interface ProductService {
    @GET("products")
    fun getProducts(@Query("limit") limit: Int, @Query("scroll-count") scrollCount: Int): Call<List<ProductResponseDTO>>
}
