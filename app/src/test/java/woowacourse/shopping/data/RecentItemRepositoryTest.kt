package woowacourse.shopping.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.localdb.dao.RecentItemDao
import woowacourse.shopping.data.localdb.entity.RecentItemEntity
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class RecentItemRepositoryTest {
    private lateinit var recentItemDao: TestRecentItemDao
    private lateinit var repository: RecentItemRepository

    @BeforeEach
    fun setUp() {
        recentItemDao = TestRecentItemDao()
        repository = RecentItemRepositoryImpl(recentItemDao)
    }

    @Test
    fun `최근 본 상품 목록을 추가할 수 있다`() =
        runTest {
            (1L..2L).forEach {
                repository.addRecentItem(createProduct(it))
            }

            val recentItems = repository.getRecentItems().first()

            assertThat(recentItems.map { it.productId }).containsExactly(2L, 1L)
        }

    @Test
    fun `최근 본 상품 목록을 최신순으로 조회할 수 있다`() =
        runTest {
            (1L..2L).forEach {
                recentItemDao.insert(
                    recentItemEntity(
                        productId = it,
                        timestamp = it,
                    ),
                )
            }

            val recentItems = repository.getRecentItems().first()

            assertThat(recentItems.map { it.productId }).containsExactly(2L, 1L)
        }

    @Test
    fun `최근 본 상품 목록은 최대 10개까지만 반환한다`() =
        runTest {
            (1L..11L).forEach {
                recentItemDao.insert(
                    recentItemEntity(
                        productId = it,
                        timestamp = it,
                    ),
                )
            }

            val recentItems = repository.getRecentItems().first()

            assertThat(recentItems.map { it.productId }).containsExactly(11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L)
        }

    @Test
    fun `마지막으로 본 상품 id를 반환한다`() =
        runTest {
            (1L..2L).forEach {
                recentItemDao.insert(
                    recentItemEntity(
                        productId = it,
                        timestamp = it,
                    ),
                )
            }

            val lastViewedItemId = repository.getLastViewedItemId()

            assertThat(lastViewedItemId).isEqualTo(2L)
        }

    @Test
    fun `최근 본 상품이 없으면 마지막으로 본 상품 id는 null을 반환한다`() =
        runTest {
            val lastViewedItemId = repository.getLastViewedItemId()

            assertThat(lastViewedItemId).isNull()
        }

    private fun createProduct(
        productId: Long,
        name: String = "product$productId",
        imageUrl: String = "image$productId",
    ): Product =
        Product(
            id = productId,
            name = ProductName(name),
            price = Money(1000),
            imageUrl = imageUrl,
            category = "book",
        )

    private fun recentItemEntity(
        productId: Long,
        name: String = "product$productId",
        imageUrl: String = "image$productId",
        timestamp: Long,
    ): RecentItemEntity =
        RecentItemEntity(
            productId = productId,
            name = name,
            imageUrl = imageUrl,
            timestamp = timestamp,
        )

    private class TestRecentItemDao : RecentItemDao {
        private val items = MutableStateFlow<List<RecentItemEntity>>(emptyList())

        override suspend fun insert(item: RecentItemEntity) {
            items.value = items.value.filterNot { it.productId == item.productId } + item
        }

        override fun getRecentItems(limit: Int): Flow<List<RecentItemEntity>> = items.map { entities -> entities.recently(limit) }

        override suspend fun getRecentItemByProductId(productId: Long): RecentItemEntity? =
            items.value.firstOrNull { it.productId == productId }

        override suspend fun deleteItemsExceedingLimit(limit: Int) {
            val recentProductIds =
                items.value
                    .recently(limit)
                    .map { it.productId }
                    .toSet()
            items.value = items.value.filter { it.productId in recentProductIds }
        }

        override suspend fun getLastViewedItem(): RecentItemEntity? = items.value.recently(limit = 1).firstOrNull()

        private fun List<RecentItemEntity>.recently(limit: Int): List<RecentItemEntity> =
            sortedWith(compareByDescending<RecentItemEntity> { it.timestamp }.thenByDescending { it.productId })
                .take(limit)
    }
}
