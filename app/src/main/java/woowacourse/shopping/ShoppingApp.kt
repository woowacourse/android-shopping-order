package woowacourse.shopping

import ProductRemoteDataSourceImpl
import android.app.Application
import woowacourse.shopping.data.database.ShoppingDatabase
import woowacourse.shopping.data.datasource.cart.CartRemoteDataSource
import woowacourse.shopping.data.datasource.cart.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.history.HistoryLocalDataSource
import woowacourse.shopping.data.datasource.history.HistoryLocalDataSourceImpl
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.di.NetworkModule.cartApi
import woowacourse.shopping.data.di.NetworkModule.couponApi
import woowacourse.shopping.data.di.NetworkModule.orderApi
import woowacourse.shopping.data.di.NetworkModule.productApi
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.CalculateCouponDiscountUseCase
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsByIdsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.IsFreeShippingCouponUseCase
import woowacourse.shopping.domain.usecase.OrderProductsUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase

class ShoppingApp : Application() {
    private val database: ShoppingDatabase by lazy { ShoppingDatabase.getInstance(this) }

    private val cartRemoteDataSource: CartRemoteDataSource by lazy {
        CartRemoteDataSourceImpl(cartApi)
    }

    private val couponRemoteDataSource: CouponRemoteDataSource by lazy {
        CouponRemoteDataSourceImpl(couponApi)
    }

    private val historyLocalDataSource: HistoryLocalDataSource by lazy {
        HistoryLocalDataSourceImpl(database.historyDao())
    }

    private val orderRemoteDataSource: OrderRemoteDataSource by lazy {
        OrderRemoteDataSourceImpl(orderApi)
    }

    private val productRemoteDataSource: ProductRemoteDataSource by lazy {
        ProductRemoteDataSourceImpl(productApi)
    }

    private val cartRepositoryImpl: woowacourse.shopping.domain.repository.CartRepository by lazy {
        woowacourse.shopping.data.repository
            .CartRepositoryImpl(cartRemoteDataSource)
    }

    private val productRepositoryImpl: woowacourse.shopping.domain.repository.ProductRepository by lazy {
        woowacourse.shopping.data.repository
            .ProductRepositoryImpl(productRemoteDataSource)
    }

    private val historyRepositoryImpl: woowacourse.shopping.domain.repository.HistoryRepository by lazy {
        woowacourse.shopping.data.repository
            .HistoryRepositoryImpl(historyLocalDataSource)
    }

    private val orderRepositoryImpl: woowacourse.shopping.domain.repository.OrderRepository by lazy {
        woowacourse.shopping.data.repository
            .OrderRepositoryImpl(orderRemoteDataSource)
    }

    private val couponRepositoryImpl: woowacourse.shopping.domain.repository.CouponRepository by lazy {
        woowacourse.shopping.data.repository
            .CouponRepositoryImpl(couponRemoteDataSource)
    }

    val getCartProductsUseCase by lazy {
        GetCartProductsUseCase(cartRepositoryImpl)
    }

    val increaseCartProductQuantityUseCase by lazy {
        IncreaseCartProductQuantityUseCase(cartRepositoryImpl)
    }

    val decreaseCartProductQuantityUseCase by lazy {
        DecreaseCartProductQuantityUseCase(cartRepositoryImpl)
    }

    val removeCartProductUseCase by lazy {
        RemoveCartProductUseCase(cartRepositoryImpl)
    }

    val updateCartProductUseCase by lazy {
        UpdateCartProductUseCase(cartRepositoryImpl)
    }

    val getSearchHistoryUseCase by lazy {
        GetSearchHistoryUseCase(historyRepositoryImpl)
    }

    val addSearchHistoryUseCase by lazy {
        AddSearchHistoryUseCase(historyRepositoryImpl)
    }

    val getRecentSearchHistoryUseCase by lazy {
        GetRecentSearchHistoryUseCase(historyRepositoryImpl)
    }

    val getCatalogProductsUseCase by lazy {
        GetCatalogProductsUseCase(productRepositoryImpl, cartRepositoryImpl)
    }

    val getCatalogProductUseCase by lazy {
        GetCatalogProductUseCase(productRepositoryImpl, cartRepositoryImpl)
    }

    val getCatalogProductsByIdsUseCase by lazy {
        GetCatalogProductsByIdsUseCase(productRepositoryImpl, cartRepositoryImpl)
    }

    val getCartProductsQuantityUseCase by lazy {
        GetCartProductsQuantityUseCase(cartRepositoryImpl)
    }

    val getCartRecommendProductsUseCase by lazy {
        GetCartRecommendProductsUseCase(
            productRepositoryImpl,
            cartRepositoryImpl,
            historyRepositoryImpl,
        )
    }

    val orderProductsUseCase by lazy {
        OrderProductsUseCase(productRepositoryImpl, cartRepositoryImpl, orderRepositoryImpl)
    }
    val getCouponsUseCase by lazy {
        GetCouponsUseCase(couponRepositoryImpl)
    }
    val calculatePaymentAmountByCouponUseCase by lazy {
        CalculatePaymentAmountByCouponUseCase(couponRepositoryImpl)
    }

    val calculateCouponDiscountUseCase by lazy {
        CalculateCouponDiscountUseCase(couponRepositoryImpl)
    }

    val isFreeShippingCouponUseCase by lazy {
        IsFreeShippingCouponUseCase(couponRepositoryImpl)
    }
}
