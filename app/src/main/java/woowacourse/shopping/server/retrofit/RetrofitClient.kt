package woowacourse.shopping.server.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.server.Server

object RetrofitClient {

    private lateinit var baseUrl: String

    val payService: PayService by lazy {
        instance.create(PayService::class.java)
    }
    val productsService: ProductsService by lazy {
        instance.create(ProductsService::class.java)
    }
    val cartItemsService: CartItemsService by lazy {
        instance.create(CartItemsService::class.java)
    }
    val membersService: MembersService by lazy {
        instance.create(MembersService::class.java)
    }

    private fun getRetrofit() = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val instance: Retrofit
        get() = getRetrofit()

    fun initBaseUrl(server: String) {
        baseUrl = Server.getUrl(server)
    }
}
