package woowacourse.shopping.util.inject

import android.content.Context
import woowacourse.shopping.data.dao.recentproduct.RecentProductDaoImpl
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultOrderRepository
import woowacourse.shopping.data.repository.DefaultPointRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.data.repository.DefaultRecentProductRepository
import woowacourse.shopping.data.util.cartService
import woowacourse.shopping.data.util.orderService
import woowacourse.shopping.data.util.pointService
import woowacourse.shopping.data.util.productService
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.ui.cart.CartContract
import woowacourse.shopping.ui.cart.CartPresenter
import woowacourse.shopping.ui.order.OrderContract
import woowacourse.shopping.ui.order.OrderPresenter
import woowacourse.shopping.ui.orderdetail.OrderDetailContract
import woowacourse.shopping.ui.orderdetail.OrderDetailPresenter
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract
import woowacourse.shopping.ui.orderhistory.OrderHistoryPresenter
import woowacourse.shopping.ui.productdetail.ProductDetailContract
import woowacourse.shopping.ui.productdetail.ProductDetailPresenter
import woowacourse.shopping.ui.serversetting.ServerSettingContract
import woowacourse.shopping.ui.serversetting.ServerSettingPresenter
import woowacourse.shopping.ui.shopping.ShoppingContract
import woowacourse.shopping.ui.shopping.ShoppingPresenter
import woowacourse.shopping.util.preference.BasePreference

fun injectShoppingPresenter(
    view: ShoppingContract.View,
    context: Context,
): ShoppingContract.Presenter {
    return ShoppingPresenter(
        view = view,
        productRepository = DefaultProductRepository(productService),
        recentProductRepository = DefaultRecentProductRepository(
            dao = RecentProductDaoImpl(injectShoppingDatabase(context))
        ),
        cartRepository = DefaultCartRepository(cartService),
    )
}

fun injectProductDetailPresenter(
    context: Context,
    view: ProductDetailContract.View,
    detailProduct: ProductModel,
    showLastViewedProduct: Boolean,
): ProductDetailContract.Presenter = ProductDetailPresenter(
    view = view,
    product = detailProduct,
    recentProductRepository = DefaultRecentProductRepository(
        dao = RecentProductDaoImpl(injectShoppingDatabase(context))
    ),
    showLastViewedProduct = showLastViewedProduct,
)

fun injectCartPresenter(
    view: CartContract.View,
): CartPresenter {
    return CartPresenter(
        view = view,
        cartRepository = DefaultCartRepository(cartService),
    )
}

fun injectServerSettingPresenter(
    view: ServerSettingContract.View,
    shoppingPreference: BasePreference,
): ServerSettingContract.Presenter = ServerSettingPresenter(
    view,
    shoppingPreference,
)

fun injectOrderPresenter(
    view: OrderContract.View,
    order: OrderModel,
): OrderContract.Presenter = OrderPresenter(
    view = view,
    order = order,
    orderRepository = DefaultOrderRepository(orderService),
    pointRepository = DefaultPointRepository(pointService),
)

fun injectOrderListPresenter(
    view: OrderHistoryContract.View,
): OrderHistoryContract.Presenter = OrderHistoryPresenter(
    view = view,
    orderRepository = DefaultOrderRepository(orderService),
)

fun injectOrderDetailPresenter(
    view: OrderDetailContract.View,
    order: OrderModel,
): OrderDetailContract.Presenter = OrderDetailPresenter(
    view = view,
    order = order,
)
