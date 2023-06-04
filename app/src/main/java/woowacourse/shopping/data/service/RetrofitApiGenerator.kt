package woowacourse.shopping.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.service.cart.CartRetrofitApi
import woowacourse.shopping.data.service.order.OrderRetrofitApi
import woowacourse.shopping.data.service.product.ProductRetrofitApi
import woowacourse.shopping.user.ServerInfo

object RetrofitApiGenerator {
    private val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(ServerInfo.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val cartService: CartRetrofitApi =
        retrofit.create(CartRetrofitApi::class.java)
    val productService: ProductRetrofitApi = retrofit.create(ProductRetrofitApi::class.java)
    val orderService: OrderRetrofitApi = retrofit.create(OrderRetrofitApi::class.java)
}
