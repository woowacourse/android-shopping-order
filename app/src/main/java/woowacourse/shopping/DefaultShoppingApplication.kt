package woowacourse.shopping

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.datasource.DefaultRemoteCartDataSource
import woowacourse.shopping.data.datasource.DefaultRemoteCouponDataSource
import woowacourse.shopping.data.datasource.DefaultRemoteOrderDataSource
import woowacourse.shopping.data.datasource.DefaultRemoteProductDataSource
import woowacourse.shopping.data.local.database.RecentProductDatabase
import woowacourse.shopping.data.local.preferences.ShoppingPreferencesManager
import woowacourse.shopping.data.model.coupon.CouponResponseItem
import woowacourse.shopping.data.remote.BasicAuthInterceptor
import woowacourse.shopping.data.remote.CartService
import woowacourse.shopping.data.remote.CouponResponseItemDeserializer
import woowacourse.shopping.data.remote.CouponService
import woowacourse.shopping.data.remote.OrderService
import woowacourse.shopping.data.remote.ProductService
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.CartViewModelFactory
import woowacourse.shopping.view.detail.DetailViewModelFactory
import woowacourse.shopping.view.home.HomeViewModelFactory
import woowacourse.shopping.view.order.OrderViewModelFactory

class DefaultShoppingApplication : ShoppingApplication() {
    private val shoppingPreferencesManager: ShoppingPreferencesManager by lazy {
        ShoppingPreferencesManager(this)
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(shoppingPreferencesManager))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .build()
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(CouponResponseItem::class.java, CouponResponseItemDeserializer)
        .create()

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    private val recentProductDatabase by lazy { RecentProductDatabase.getInstance(this) }
    private val productService by lazy { retrofit.create(ProductService::class.java) }
    private val cartService by lazy { retrofit.create(CartService::class.java) }
    private val orderService by lazy { retrofit.create(OrderService::class.java) }
    private val couponService by lazy { retrofit.create(CouponService::class.java) }
    private val remoteCartDataSource by lazy { DefaultRemoteCartDataSource(cartService) }
    private val remoteProductDataSource by lazy { DefaultRemoteProductDataSource(productService) }
    private val remoteOrderDataSource by lazy { DefaultRemoteOrderDataSource(orderService) }
    private val remoteCouponDataSource by lazy { DefaultRemoteCouponDataSource(couponService) }

    override val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            remoteProductDataSource,
            remoteCartDataSource,
            recentProductDatabase.recentProductDao(),
        )
    }
    override val cartRepository: CartRepository by lazy { CartRepositoryImpl(remoteCartDataSource) }
    override val couponRepository: CouponRepository by lazy {
        CouponRepositoryImpl(remoteCouponDataSource)
    }
    override val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(remoteOrderDataSource)
    }
    override val recentProductRepository: RecentProductRepository by lazy {
        RecentProductRepositoryImpl(recentProductDatabase)
    }
    override val homeViewModelFactory: HomeViewModelFactory by lazy {
        HomeViewModelFactory(productRepository, cartRepository, recentProductRepository)
    }
    override val cartViewModelFactory: CartViewModelFactory by lazy {
        CartViewModelFactory(cartRepository, recentProductRepository, productRepository)
    }

    override fun detailViewModelFactory(productId: Int): DetailViewModelFactory =
        DetailViewModelFactory(
            cartRepository = cartRepository,
            productRepository = productRepository,
            recentProductRepository = recentProductRepository,
            productId = productId,
        )

    override fun orderViewModelFactory(cartItems: List<CartItemDomain>): OrderViewModelFactory =
        OrderViewModelFactory(couponRepository, orderRepository, cartItems)
}
