package woowacourse.shopping.util.inject

import android.content.Context
import woowacourse.shopping.data.dao.recentproduct.RecentProductDaoImpl
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.PointRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.util.cartService
import woowacourse.shopping.data.util.orderService
import woowacourse.shopping.data.util.pointService
import woowacourse.shopping.data.util.productService
import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.ui.cart.CartContract
import woowacourse.shopping.ui.cart.CartPresenter
import woowacourse.shopping.ui.detail.ProductDetailContract
import woowacourse.shopping.ui.detail.ProductDetailPresenter
import woowacourse.shopping.ui.order.OrderContract
import woowacourse.shopping.ui.order.OrderPresenter
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
        productRepository = ProductRepositoryImpl(productService),
        recentProductRepository = RecentProductRepositoryImpl(
            dao = RecentProductDaoImpl(createShoppingDatabase(context))
        ),
        cartRepository = CartRepositoryImpl(cartService),
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
    recentProductRepository = RecentProductRepositoryImpl(
        dao = RecentProductDaoImpl(createShoppingDatabase(context))
    ),
    showLastViewedProduct = showLastViewedProduct,
)

fun injectCartPresenter(
    view: CartContract.View,
): CartPresenter {
    return CartPresenter(
        view = view,
        cartRepository = CartRepositoryImpl(cartService),
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
    order: UiOrder,
): OrderContract.Presenter = OrderPresenter(
    view = view,
    order = order,
    orderRepository = OrderRepositoryImpl(orderService),
    pointRepository = PointRepositoryImpl(pointService),
)
