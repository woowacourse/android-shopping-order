package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.repository.ProductRepository

class RecommendViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {
    init {
        fetchData()
    }

    fun fetchData() {}
}
