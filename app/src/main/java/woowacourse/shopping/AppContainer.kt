package woowacourse.shopping

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.cart.CartItemRepository
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.cart.source.CartItemRemoteService
import woowacourse.shopping.data.cart.source.DefaultNetworkCartItemDataSource
import woowacourse.shopping.data.discount.DefaultDiscountInfoRepository
import woowacourse.shopping.data.discount.source.DefaultNetworkDiscountInfoDataSource
import woowacourse.shopping.data.discount.source.DiscountInfoRemoteService
import woowacourse.shopping.data.network.AuthorizationBoxingInterceptor
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.order.source.DefaultNetworkOrderDataSource
import woowacourse.shopping.data.order.source.OrderRemoteService
import woowacourse.shopping.utils.ServerConfiguration

class AppContainer {

    private val authorizationBoxingInterceptor = AuthorizationBoxingInterceptor()

    private val okHttpClientAddedAuthorizationBoxingInterceptor = OkHttpClient.Builder()
        .addInterceptor(authorizationBoxingInterceptor)
        .build()

    private val gsonConverterFactory = GsonConverterFactory.create()

    private val cartItemRemoteService = Retrofit.Builder()
        .baseUrl(ServerConfiguration.host.url)
        .client(okHttpClientAddedAuthorizationBoxingInterceptor)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(CartItemRemoteService::class.java)

    private val networkCartItemDataSource = DefaultNetworkCartItemDataSource(cartItemRemoteService)

    val cartItemRepository: CartItemRepository =
        DefaultCartItemRepository(networkCartItemDataSource)

    private val orderRemoteService = Retrofit.Builder()
        .baseUrl(ServerConfiguration.host.url)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(OrderRemoteService::class.java)

    private val networkOrderDataSource = DefaultNetworkOrderDataSource(orderRemoteService)

    val orderRepository = DefaultOrderRepository(networkOrderDataSource)

    private val discountInfoRemoteService = Retrofit.Builder()
        .baseUrl(ServerConfiguration.host.url)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(DiscountInfoRemoteService::class.java)

    private val networkDiscountInfoDataSource =
        DefaultNetworkDiscountInfoDataSource(discountInfoRemoteService)

    val discountInfoRepository = DefaultDiscountInfoRepository(networkDiscountInfoDataSource)
}
