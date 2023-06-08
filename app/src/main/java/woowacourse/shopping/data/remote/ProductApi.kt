package woowacourse.shopping.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.data.remote.dto.ProductDTO
import woowacourse.shopping.data.remote.dto.ProductWithCartInfoDTO
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO

interface ProductApi {
    @GET("products")
    fun requestProducts(): Call<List<ProductDTO>>

    @GET("products/cart-items")
    fun requestProductsByRange(@Query("lastId") lastId: Int, @Query("pageItemCount") pageItemCount: Int): Call<ProductsWithCartItemDTO>

    @GET("products/{id}/cart-items")
    fun requestProductById(@Path("id") id: Int): Call<ProductWithCartInfoDTO>
}
