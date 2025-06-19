package woowacourse.shopping.feature.goodsdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.local.history.repository.HistoryRepository
import woowacourse.shopping.data.remote.cart.CartRepository
import woowacourse.shopping.data.remote.product.ProductRepository

class GoodsDetailsViewModelFactory(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val productRepository: ProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoodsDetailsViewModel::class.java)) {
            return GoodsDetailsViewModel(
                cartRepository,
                historyRepository,
                productRepository,
            ) as T
        }
        return super.create(modelClass)
    }
}
