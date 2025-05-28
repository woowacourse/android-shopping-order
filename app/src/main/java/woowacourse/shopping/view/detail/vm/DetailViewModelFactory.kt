package woowacourse.shopping.view.detail.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.repository.HistoryRepository

class DetailViewModelFactory(
    private val defaultProductRepository: DefaultProductRepository,
    private val defaultCartRepository: DefaultCartRepository,
    private val historyRepository: HistoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(defaultProductRepository, defaultCartRepository, historyRepository) as T
        }
        throw IllegalArgumentException()
    }
}
