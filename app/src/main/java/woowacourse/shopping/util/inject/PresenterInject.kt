package woowacourse.shopping.util.inject

import android.content.Context
import woowacourse.shopping.data.dao.recentproduct.RecentProductDaoImpl
import woowacourse.shopping.data.datasource.cart.CartProductRemoteDataSource
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.point.PointRemoteDataSource
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.repository.RecentProductLocalRepository
import woowacourse.shopping.data.repository.remote.CartProductRemoteRepository
import woowacourse.shopping.data.repository.remote.OrderProductRemoteRepository
import woowacourse.shopping.data.repository.remote.PointRemoteRepository
import woowacourse.shopping.data.repository.remote.ProductRemoteRepository
import woowacourse.shopping.data.util.RetrofitUtil
import woowacourse.shopping.data.util.RetrofitUtil.getCartProductByRetrofit
import woowacourse.shopping.data.util.RetrofitUtil.getOrderProductByRetrofit
import woowacourse.shopping.data.util.RetrofitUtil.getProductByRetrofit
import woowacourse.shopping.model.CartProducts
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.ui.cart.CartContract
import woowacourse.shopping.ui.cart.CartPresenter
import woowacourse.shopping.ui.detail.ProductDetailContract
import woowacourse.shopping.ui.detail.ProductDetailPresenter
import woowacourse.shopping.ui.order.detail.OrderDetailContract
import woowacourse.shopping.ui.order.detail.OrderDetailPresenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract
import woowacourse.shopping.ui.order.history.OrderHistoryPresenter
import woowacourse.shopping.ui.order.main.OrderContract
import woowacourse.shopping.ui.order.main.OrderPresenter
import woowacourse.shopping.ui.serversetting.ServerSettingContract
import woowacourse.shopping.ui.serversetting.ServerSettingPresenter
import woowacourse.shopping.ui.shopping.ShoppingContract
import woowacourse.shopping.ui.shopping.ShoppingPresenter
import woowacourse.shopping.util.preference.BasePreference

fun inject(
    view: ShoppingContract.View,
    context: Context,
    baseUrl: String,
): ShoppingContract.Presenter {
    return ShoppingPresenter(
        view = view,
        productRepository = ProductRemoteRepository(
            ProductRemoteDataSource(
                getProductByRetrofit(baseUrl),
            ),
        ),
        recentProductRepository = RecentProductLocalRepository(
            dao = RecentProductDaoImpl(createShoppingDatabase(context)),
        ),
        cartRepository = CartProductRemoteRepository(
            CartProductRemoteDataSource(
                getCartProductByRetrofit(baseUrl),
            ),
        ),
    )
}

fun inject(
    context: Context,
    view: ProductDetailContract.View,
    detailProduct: UiProduct,
    showLastViewedProduct: Boolean,
): ProductDetailContract.Presenter = ProductDetailPresenter(
    view = view,
    product = detailProduct,
    recentProductRepository = RecentProductLocalRepository(
        dao = RecentProductDaoImpl(createShoppingDatabase(context)),
    ),
    showLastViewedProduct = showLastViewedProduct,
)

fun injectCartPresenter(
    view: CartContract.View,
    baseUrl: String,
): CartPresenter {
    return CartPresenter(
        view = view,
        cartRepository = CartProductRemoteRepository(
            CartProductRemoteDataSource(
                getCartProductByRetrofit(baseUrl),
            ),
        ),
    )
}

fun inject(
    view: ServerSettingContract.View,
    shoppingPreference: BasePreference,
): ServerSettingContract.Presenter = ServerSettingPresenter(
    view,
    shoppingPreference,
)

fun injectOrderPresenter(
    view: OrderContract.View,
    cartProducts: CartProducts?,
    baseUrl: String,
): OrderContract.Presenter = OrderPresenter(
    view = view,
    cartProducts = cartProducts?.items ?: listOf(),
    orderProductRepository = OrderProductRemoteRepository(
        OrderRemoteDataSource(
            getOrderProductByRetrofit(baseUrl),
        ),
    ),
    pointRepository = PointRemoteRepository(
        PointRemoteDataSource(
            RetrofitUtil.getPointByRetrofit(baseUrl),
        ),
    ),
)

fun injectOrderHistoryPresenter(
    view: OrderHistoryContract.View,
    baseUrl: String,
): OrderHistoryContract.Presenter = OrderHistoryPresenter(
    view = view,
    orderProductRepository = OrderProductRemoteRepository(
        OrderRemoteDataSource(
            getOrderProductByRetrofit(baseUrl),
        ),
    ),
)

fun injectOrderDetailPresenter(
    view: OrderDetailContract.View,
    orderId: Int,
    baseUrl: String,
): OrderDetailContract.Presenter = OrderDetailPresenter(
    view = view,
    orderId = orderId,
    orderProductRepository = OrderProductRemoteRepository(
        OrderRemoteDataSource(
            getOrderProductByRetrofit(baseUrl),
        ),
    ),
)
