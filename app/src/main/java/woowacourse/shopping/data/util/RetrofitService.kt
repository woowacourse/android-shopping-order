package woowacourse.shopping.data.util

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<GoodsResponse>
}
