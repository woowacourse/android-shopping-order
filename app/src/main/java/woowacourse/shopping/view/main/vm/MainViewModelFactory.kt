package woowacourse.shopping.view.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository

class MainViewModelFactory(
    private val historyRepository: HistoryRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                historyRepository,
                productRepository,
                cartRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
