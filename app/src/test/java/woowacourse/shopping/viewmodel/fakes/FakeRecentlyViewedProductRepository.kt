package woowacourse.shopping.viewmodel.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import woowacourse.shopping.data.local.entity.RecentlyViewedProductEntity
import woowacourse.shopping.data.local.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.Product

class FakeRecentlyViewedProductRepository : RecentlyViewedProductRepository {
    private val _history = MutableStateFlow<List<Long>>(emptyList())

    override fun getAll(): Flow<List<Long>?> = _history

    override suspend fun updateList(product: Product) {
        val current = _history.value.toMutableList()
        current.removeAll { it == product.id }
        current.add(0, product.id)
        if (current.size > 10) {
            _history.value = current.take(10)
        } else {
            _history.value = current
        }
    }

    override fun getLatestItem(): Flow<Long?> = MutableStateFlow(_history.value.firstOrNull())
}
