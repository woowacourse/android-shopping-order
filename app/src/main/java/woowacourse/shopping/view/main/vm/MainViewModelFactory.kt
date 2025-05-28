package woowacourse.shopping.view.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.view.loader.HistoryLoader

class MainViewModelFactory(
    private val historyRepository: HistoryRepository,
    private val historyLoader: HistoryLoader,
    private val cartRepository: DefaultCartRepository,
    private val productRepository: DefaultProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                historyRepository,
                historyLoader,
                productRepository,
                cartRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
