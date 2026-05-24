package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.data.local.repository.OutstandingProductRepository

class FakeOutstandingProductRepository : OutstandingProductRepository {
    private val outstandingIds = mutableListOf<Long>()

    override suspend fun getAll(): List<Long> {
        return outstandingIds.toList()
    }

    override suspend fun insertAll(cartItemIds: List<Long>) {
        outstandingIds.addAll(cartItemIds)
    }

    override suspend fun deleteAll() {
        outstandingIds.clear()
    }
}
