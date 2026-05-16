package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository

class FakeRecentProductRepository(
    initial: List<String> = emptyList(),
) : RecentProductRepository {

    private val ids: ArrayDeque<String> = ArrayDeque(initial)

    override suspend fun loadProducts(): List<String> = ids.toList()

    override suspend fun insert(id: String) {
        ids.remove(id)
        ids.addFirst(id)
    }
}
