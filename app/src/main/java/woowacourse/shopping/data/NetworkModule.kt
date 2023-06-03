package woowacourse.shopping.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.datasource.basket.BasketProductService
import woowacourse.shopping.data.datasource.order.OrderService
import woowacourse.shopping.data.datasource.product.ProductService
import woowacourse.shopping.data.datasource.user.UserService
import java.util.Base64

object NetworkModule {

    const val AUTHORIZATION_FORMAT = "Basic %s"
    val encodedUserInfo =
        Base64.getEncoder().encodeToString("a@a.com:1234".toByteArray(Charsets.UTF_8))
    private lateinit var url: String
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(NullOnEmptyConvertFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val basketProductService: BasketProductService by lazy { getService() }
    val orderService: OrderService by lazy { getService() }
    val productService: ProductService by lazy { getService() }
    val userService: UserService by lazy { getService() }

    fun setBaseUrl(url: String) {
        this.url = url
    }

    private inline fun <reified T> getService(): T {

        return retrofit.create(T::class.java)
    }
}
