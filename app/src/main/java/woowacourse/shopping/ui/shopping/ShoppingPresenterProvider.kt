package woowacourse.shopping.ui.shopping

import android.content.Context
import woowacourse.shopping.data.ShoppingRetrofit
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

object ShoppingPresenterProvider {
    fun create(view: ShoppingContract.View, context: Context): ShoppingContract.Presenter {
        val productRemoteSource = ProductRemoteSource(ShoppingRetrofit.retrofit)
        val dbHelper = DbHelper.getDbInstance(context)
        val recentlyViewedProductMemorySource = RecentlyViewedProductMemorySource(dbHelper)
        val defaultRecentlyViewedProductRepository = DefaultRecentlyViewedProductRepository(
            recentlyViewedProductMemorySource, productRemoteSource
        )
        val defaultCartItemRepository = DefaultCartItemRepository(
            CartItemRemoteSource(ShoppingRetrofit.retrofit)
        )
        val defaultProductRepository = DefaultProductRepository(productRemoteSource)
        val userRepository = DefaultUserRepository(UserMemorySource(), UserRemoteSource())
        return ShoppingPresenter(
            view,
            defaultRecentlyViewedProductRepository,
            defaultProductRepository,
            defaultCartItemRepository,
            userRepository
        )
    }
}
