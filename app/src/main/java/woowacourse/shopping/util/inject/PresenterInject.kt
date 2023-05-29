package woowacourse.shopping.util.inject

import android.content.Context
import woowacourse.shopping.data.dao.recentproduct.RecentProductDaoImpl
import woowacourse.shopping.data.datasource.product.ProductDataSourceImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.okhttp.CartRepositoryImpl
import woowacourse.shopping.data.repository.retrofit.ProductRepositoryImpl
import woowacourse.shopping.data.service.okhttp.cart.CartServiceImpl
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.ui.cart.CartContract
import woowacourse.shopping.ui.cart.CartPresenter
import woowacourse.shopping.ui.detail.ProductDetailContract
import woowacourse.shopping.ui.detail.ProductDetailPresenter
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
        productRepository = ProductRepositoryImpl(ProductDataSourceImpl()),
        recentProductRepository = RecentProductRepositoryImpl(
            dao = RecentProductDaoImpl(createShoppingDatabase(context)),
        ),
        cartRepository = CartRepositoryImpl(CartServiceImpl()),
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
        dao = RecentProductDaoImpl(createShoppingDatabase(context)),
    ),
    showLastViewedProduct = showLastViewedProduct,
)

fun injectCartPresenter(
    view: CartContract.View,
): CartPresenter {
    return CartPresenter(
        view = view,
        cartRepository = CartRepositoryImpl(CartServiceImpl()),
    )
}

fun inject(
    view: ServerSettingContract.View,
    shoppingPreference: BasePreference,
): ServerSettingContract.Presenter = ServerSettingPresenter(
    view,
    shoppingPreference,
)
