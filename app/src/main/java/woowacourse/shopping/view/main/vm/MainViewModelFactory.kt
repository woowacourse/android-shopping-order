package woowacourse.shopping.view.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.view.loader.HistoryLoader

class MainViewModelFactory(
    private val cartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
    private val historyLoader: HistoryLoader,
    private val defaultProductRepository: DefaultProductRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                cartRepository,
                historyRepository,
                historyLoader,
                defaultProductRepository,
            ) as T
        }
        throw IllegalArgumentException()
    }
}
