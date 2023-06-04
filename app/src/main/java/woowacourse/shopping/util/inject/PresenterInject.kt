package woowacourse.shopping.util.inject

import android.content.Context
import woowacourse.shopping.data.dao.recentproduct.RecentProductDaoImpl
import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.repository.RecentProductLocalRepository
import woowacourse.shopping.data.repository.retrofit.CartProductRemoteRepository
import woowacourse.shopping.data.repository.retrofit.OrderProductRemoteRepository
import woowacourse.shopping.data.repository.retrofit.PointRemoteRepository
import woowacourse.shopping.data.repository.retrofit.ProductRemoteRepository
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
): ShoppingContract.Presenter {
    return ShoppingPresenter(
        view = view,
        productRepository = ProductRemoteRepository(ProductRemoteDataSource()),
        recentProductRepository = RecentProductLocalRepository(
            dao = RecentProductDaoImpl(createShoppingDatabase(context)),
        ),
        cartRepository = CartProductRemoteRepository(),
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
): CartPresenter {
    return CartPresenter(
        view = view,
        cartRepository = CartProductRemoteRepository(),
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
    cartProducts: CartProducts,
): OrderContract.Presenter = OrderPresenter(
    view = view,
    cartProducts = cartProducts.items,
    orderProductRepository = OrderProductRemoteRepository(),
    pointRepository = PointRemoteRepository(),
)

fun injectOrderHistoryPresenter(
    view: OrderHistoryContract.View,
): OrderHistoryContract.Presenter = OrderHistoryPresenter(
    view = view,
    orderProductRepository = OrderProductRemoteRepository(),
)

fun injectOrderDetailPresenter(
    view: OrderDetailContract.View,
    orderId: Int,
): OrderDetailContract.Presenter = OrderDetailPresenter(
    view = view,
    orderId = orderId,
    orderProductRepository = OrderProductRemoteRepository(),
)
