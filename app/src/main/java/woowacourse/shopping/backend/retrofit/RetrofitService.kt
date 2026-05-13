package woowacourse.shopping.backend.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.backend.retrofit.api.OrderRetrofitInterface
import woowacourse.shopping.backend.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.backend.retrofit.api.ShoppingCartRetrofitInterface

object RetrofitService {
    private const val BASE_URL = "http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/"

    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val orderApiService: OrderRetrofitInterface = retrofit.create(OrderRetrofitInterface::class.java)
    val productApiService: ProductRetrofitInterface = retrofit.create(ProductRetrofitInterface::class.java)
    val shoppingCartApiService: ShoppingCartRetrofitInterface = retrofit.create(ShoppingCartRetrofitInterface::class.java)
}
