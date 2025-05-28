package woowacourse.shopping.data.remote.product

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ProductService {
    @GET("/products")
    fun requestGoods(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Call<ProductResponse>
}
