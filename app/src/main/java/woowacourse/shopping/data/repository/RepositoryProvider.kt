package woowacourse.shopping.data.repository

import android.content.Context
import woowacourse.shopping.data.api.ApiClient
import woowacourse.shopping.data.datasource.CartItemDataSourceImpl
import woowacourse.shopping.data.datasource.CouponDataSourceImpl
import woowacourse.shopping.data.datasource.ProductDataSourceImpl
import woowacourse.shopping.data.db.ShoppingDatabase

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

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        val database = ShoppingDatabase.getDatabase(context.applicationContext)
        val recentProductDao = database.recentProductDao()

        val productDataSource = ProductDataSourceImpl(ApiClient.productService)
        val cartItemDataSource = CartItemDataSourceImpl(ApiClient.cartItemService)
        val couponDataSource = CouponDataSourceImpl(ApiClient.couponService)

        _productRepository =
            ProductRepositoryImpl(
                recentProductDao,
                productDataSource,
                cartItemDataSource,
            )
        _cartRepository = CartRepositoryImpl(cartItemDataSource)
        _couponRepository = CouponRepositoryImpl(couponDataSource)

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
}
