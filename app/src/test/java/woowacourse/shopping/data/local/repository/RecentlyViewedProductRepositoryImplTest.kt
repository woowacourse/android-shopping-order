package woowacourse.shopping.data.local.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.local.dao.RecentlyViewedProductDao
import woowacourse.shopping.data.local.entity.RecentlyViewedProductEntity
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.RecentlyViewedProductRepository

class RecentlyViewedProductRepositoryImplTest {
    private val fakeDao = FakeRecentlyViewedProductDao()
    private val repository: RecentlyViewedProductRepository = RecentlyViewedProductRepositoryImpl(fakeDao)

    @Test
    fun `최근 본 상품 목록을 상품 ID 목록으로 변환한다`() = runTest {
        // given
        fakeDao.setItems(
            listOf(
                RecentlyViewedProductEntity(id = 2L, timeStamp = 2L),
                RecentlyViewedProductEntity(id = 1L, timeStamp = 1L),
            )
        )

        // when
        val productIds = repository.getAll().first()

        // then
        assertEquals(listOf(2L, 1L), productIds)
    }

    @Test
    fun `최근 본 상품을 추가하면 DAO에 저장된다`() = runTest {
        // given
        val product = Product(
            category = "category",
            id = 1L,
            imageUri = "uri",
            name = "테스트 상품",
            price = 1000,
        )

        // when
        repository.updateList(product)

        // then
        assertEquals(listOf(1L), repository.getAll().first())
    }

    @Test
    fun `마지막으로 본 상품 ID를 조회할 수 있다`() = runTest {
        // given
        fakeDao.setItems(
            listOf(
                RecentlyViewedProductEntity(id = 3L, timeStamp = 3L),
                RecentlyViewedProductEntity(id = 2L, timeStamp = 2L),
            )
        )

        // when
        val latestItemId = repository.getLatestItem().first()

        // then
        assertEquals(3L, latestItemId)
    }

    private class FakeRecentlyViewedProductDao : RecentlyViewedProductDao {
        private val items = MutableStateFlow<List<RecentlyViewedProductEntity>?>(emptyList())

        fun setItems(newItems: List<RecentlyViewedProductEntity>) {
            items.value = newItems.sortedByDescending { it.timeStamp }
        }

        override suspend fun insert(recentlyViewedProductEntity: RecentlyViewedProductEntity) {
            val current = items.value.orEmpty().toMutableList()
            current.removeAll { it.id == recentlyViewedProductEntity.id }
            current.add(recentlyViewedProductEntity)
            items.value = current.sortedByDescending { it.timeStamp }
        }

        override suspend fun removeOldData() {
            items.value = items.value.orEmpty().take(10)
        }

        override fun getAll(): Flow<List<RecentlyViewedProductEntity>?> = items

        override fun getLatestItemId(): Flow<Long?> = items.map { entities ->
            entities?.firstOrNull()?.id
        }
    }
}
