package woowacourse.shopping.data.server

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import woowacourse.shopping.Storage
import woowacourse.shopping.data.cart.CartService
import woowacourse.shopping.data.member.MemberService
import woowacourse.shopping.data.order.OrderService
import woowacourse.shopping.data.product.ProductService

class ShoppingRetrofit private constructor(retrofit: Retrofit) {
    val productService: ProductService = retrofit.create(ProductService::class.java)
    val cartService: CartService = retrofit.create(CartService::class.java)
    val memberService: MemberService = retrofit.create(MemberService::class.java)
    val orderService: OrderService = retrofit.create(OrderService::class.java)

    companion object {
        @Volatile
        private var instance: ShoppingRetrofit? = null

        fun getInstance(storage: Storage): ShoppingRetrofit {
            return instance ?: synchronized(this) {
                instance ?: createInstance(storage)
            }
        }

        private fun createInstance(storage: Storage): ShoppingRetrofit {
            val interceptorClient = OkHttpClient().newBuilder()
                .addInterceptor(AuthorizationInterceptor(storage.credential))
                .build()

            return ShoppingRetrofit(
                Retrofit.Builder()
                    .baseUrl(Server.getUrl(storage.server))
                    .client(interceptorClient)
                    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                    .build()
            ).also { instance = it }
        }

        fun releaseInstance() {
            instance = null
        }
    }
}