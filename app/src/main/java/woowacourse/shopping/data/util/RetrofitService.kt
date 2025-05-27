package woowacourse.shopping.data.util

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.data.goods.dto.Content
import woowacourse.shopping.data.goods.dto.GoodsResponse

interface RetrofitService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<GoodsResponse>

    @GET("/products/{id}")
    fun requestProductDetail(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Long = 0,
    ): Call<Content>

    @GET("/cart-items")
    fun requestCartProduct(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
        @Query("sort") sort: String = "id,desc",
        @Header("Authorization") authorization: String,
    ): Call<CartResponse>

    @GET("/cart-items/counts")
    fun requestCartCounts(
        @Header("accept") accept: String = "*/*",
        @Header("Authorization") authorization: String,
    ): Call<CartQuantity>
}
