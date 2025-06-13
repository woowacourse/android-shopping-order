package woowacourse.shopping.feature.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository

class GoodsViewModelFactory(
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoodsViewModel::class.java)) {
            return GoodsViewModel(
                historyRepository,
                productRepository,
                cartRepository,
            ) as T
        }
        throw IllegalArgumentException("")
    }
}
