package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.api.ApiClient
import woowacourse.shopping.data.datasource.cart.CartDataSourceImpl
import woowacourse.shopping.data.datasource.coupon.CouponDataSourceImpl
import woowacourse.shopping.data.datasource.order.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.product.ProductDataSourceImpl
import woowacourse.shopping.data.db.ShoppingDatabase
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.data.repository.coupon.CouponRepositoryImpl
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.product.ProductRepositoryImpl

object RepositoryProvider {
    private const val NOT_INITIALIZED_MESSAGE = "%s가 초기화되지 않았습니다"

    private var _productRepository: ProductRepository? = null
    val productRepository
        get() =
            requireNotNull(_productRepository) {
                NOT_INITIALIZED_MESSAGE.format(
                    ProductRepository::class.simpleName,
                )
            }

    private var _cartRepository: CartRepository? = null
    val cartRepository
        get() =
            requireNotNull(_cartRepository) {
                NOT_INITIALIZED_MESSAGE.format(
                    CartRepository::class.simpleName,
                )
            }

    private var _couponRepository: CouponRepository? = null
    val couponRepository
        get() =
            requireNotNull(_couponRepository) {
                NOT_INITIALIZED_MESSAGE.format(
                    CouponRepository::class.simpleName,
                )
            }

    private var _orderRepository: OrderRepository? = null
    val orderRepository
        get() =
            requireNotNull(_orderRepository) {
                NOT_INITIALIZED_MESSAGE.format(
                    OrderRepository::class.simpleName,
                )
            }

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val database = ShoppingDatabase.getDatabase(context.applicationContext)
        val recentProductDao = database.recentProductDao()

        val productDataSource = ProductDataSourceImpl(ApiClient.productService)
        val cartItemDataSource = CartDataSourceImpl(ApiClient.cartItemService)
        val couponDataSource = CouponDataSourceImpl(ApiClient.couponService)
        val orderDataSource = OrderDataSourceImpl(ApiClient.orderService)

        _productRepository =
            ProductRepositoryImpl(
                recentProductDao,
                productDataSource,
                cartItemDataSource,
            )
        _cartRepository = CartRepositoryImpl(cartItemDataSource)
        _couponRepository = CouponRepositoryImpl(couponDataSource)
        _orderRepository = OrderRepositoryImpl(orderDataSource, cartItemDataSource)

        isInitialized = true
    }

    fun initProductRepository(repository: ProductRepository) {
        _productRepository = repository
    }

    fun initCartRepository(repository: CartRepository) {
        _cartRepository = repository
    }

    fun initCouponRepository(repository: CouponRepository) {
        _couponRepository = repository
    }

    fun initOrderRepository(repository: OrderRepository) {
        _orderRepository = repository
    }
}
