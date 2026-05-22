package woowacourse.shopping.viewmodel.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository

class FakeRecentlyViewedProductRepository : RecentlyViewedProductRepository {
    private val history = MutableStateFlow<List<Long>>(emptyList())

    override fun getAll(): Flow<List<Long>?> = history

    override suspend fun updateList(product: Product) {
        val current = history.value.toMutableList()
        current.removeAll { it == product.id }
        current.add(0, product.id)
        if (current.size > 10) {
            history.value = current.take(10)
        } else {
            history.value = current
        }
    }

    override fun getLatestItem(): Flow<Long?> = history.map { it.firstOrNull() }
}
