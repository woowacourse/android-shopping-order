package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository

class FakeRecentProductRepository(
    initial: List<Long> = emptyList(),
) : RecentProductRepository {

    private val recents: ArrayDeque<Long> = ArrayDeque(initial)
    val insertedIds: MutableList<Long> = mutableListOf()

    override suspend fun loadProducts(): List<Long> = recents.toList()

    override suspend fun insert(id: Long) {
        insertedIds.add(id)
        recents.remove(id)
        recents.addFirst(id)
        while (recents.size > MAX_SIZE) {
            recents.removeLast()
        }
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}
