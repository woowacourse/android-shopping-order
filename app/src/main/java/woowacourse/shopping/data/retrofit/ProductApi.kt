package woowacourse.shopping.data.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItemDTO

interface ProductApi {
    @GET("products")
    fun requestProducts(): Call<List<Product>>

    @GET("products/cart-items")
    fun requestProductsByRange(@Query("lastId") lastId: Int, @Query("pageItemCount") pageItemCount: Int): Call<ProductsWithCartItemDTO>

    @GET("products/{id}/cart-items")
    fun requestProductById(@Path("id") id: Int): Call<ProductWithCartInfo>
}
