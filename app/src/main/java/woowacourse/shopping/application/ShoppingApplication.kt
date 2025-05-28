package woowacourse.shopping.application

import android.app.Application
import woowacourse.shopping.data.local.ShoppingDatabase
import woowacourse.shopping.data.local.cart.repository.LocalCartRepositoryImpl
import woowacourse.shopping.data.local.history.repository.HistoryRepositoryImpl
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.goods.GoodsViewModel
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsViewModel
import woowacourse.shopping.util.ViewModelFactory

class ShoppingApplication : Application() {
    private val database by lazy { ShoppingDatabase.getDatabase(applicationContext) }

    val goodsFactory by lazy {
        ViewModelFactory {
            GoodsViewModel(
                HistoryRepositoryImpl(database.historyDao()),
                ProductRepository(),
                CartRepository(),
            )
        }
    }

    val goodsDetailsFactory by lazy {
        ViewModelFactory {
            GoodsDetailsViewModel(
                LocalCartRepositoryImpl(database.cartDao()),
                HistoryRepositoryImpl(database.historyDao()),
            )
        }
    }

    val cartFactory by lazy {
        ViewModelFactory {
            CartViewModel(
                CartRepository(),
            )
        }
    }
}
