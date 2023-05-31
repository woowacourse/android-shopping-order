package woowacourse.shopping.ui.productdetail

import android.content.Context
import woowacourse.shopping.network.ShoppingRetrofit
import woowacourse.shopping.data.cart.CartItemRemoteSource
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.data.product.ProductRemoteSource
import woowacourse.shopping.data.recentlyviewedproduct.DefaultRecentlyViewedProductRepository
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductMemorySource
import woowacourse.shopping.data.user.DefaultUserRepository
import woowacourse.shopping.data.user.UserMemorySource
import woowacourse.shopping.data.user.UserRemoteSource

object ProductDetailPresenterProvider {
    fun create(
        view: ProductDetailContract.View,
        context: Context
    ): ProductDetailContract.Presenter {
        return ProductDetailPresenter(
            view,
            DefaultProductRepository(ProductRemoteSource(ShoppingRetrofit.retrofit)),
            DefaultCartItemRepository(
                CartItemRemoteSource(ShoppingRetrofit.retrofit)
            ),
            DefaultUserRepository(UserMemorySource(), UserRemoteSource(ShoppingRetrofit.retrofit)),
            DefaultRecentlyViewedProductRepository(
                RecentlyViewedProductMemorySource(
                    DbHelper.getDbInstance(context)
                ),
                ProductRemoteSource(ShoppingRetrofit.retrofit)
            ),
        )
    }
}
