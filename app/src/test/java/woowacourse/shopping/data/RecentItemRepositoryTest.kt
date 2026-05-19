package woowacourse.shopping.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.repository.RecentItemRepositoryImpl
import woowacourse.shopping.mockup.MockProductRepository
import woowacourse.shopping.mockup.MockRecentItemDao
import woowacourse.shopping.mockup.createProduct
import woowacourse.shopping.mockup.toRecentItemEntity
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName

class RecentItemRepositoryTest {
    private val product =
        Product(
            id = "1",
            name = ProductName("product"),
            price = Money(2000),
            imageUrl = "image-url",
            category = "book",
        )

    @Test
    fun `최근 본 상품을 저장하고 오래된 상품을 삭제한다`() =
        runTest {
            val dao = MockRecentItemDao()
            val repository = RecentItemRepositoryImpl(dao, MockProductRepository(listOf(product)))

            repository.addRecentItem(product)

            val savedItem = dao.getRecentItemById(product.id)
            assertThat(savedItem?.id).isEqualTo(product.id)
            assertThat(dao.deleteOldItemCount).isEqualTo(1)
        }

    @Test
    fun `최근 본 상품 엔티티 목록을 도메인 상품으로 변환한다`() =
        runTest {
            val dao = MockRecentItemDao()
            val repository = RecentItemRepositoryImpl(dao, MockProductRepository(listOf(product)))
            dao.insert(product.toRecentItemEntity(timestamp = 100L))

            val recentItems = repository.getRecentItems().first()

            assertThat(recentItems).hasSize(1)
            assertThat(recentItems[0].id).isEqualTo(product.id)
            assertThat(recentItems[0].getName()).isEqualTo(product.getName())
            assertThat(recentItems[0].getPrice()).isEqualTo(product.getPrice())
            assertThat(recentItems[0].imageUrl).isEqualTo(product.imageUrl)
        }

    @Test
    fun `마지막으로 본 상품을 반환한다`() =
        runTest {
            val dao = MockRecentItemDao()
            val repository = RecentItemRepositoryImpl(dao, MockProductRepository(listOf(createProduct(id = "1"), createProduct(id = "2"))))
            dao.insert(createProduct(id = "1").toRecentItemEntity(timestamp = 100L))
            dao.insert(createProduct(id = "2").toRecentItemEntity(timestamp = 200L))

            val lastViewedItem = repository.getLastViewedItem()

            assertThat(lastViewedItem?.id).isEqualTo("2")
        }

    @Test
    fun `최근 본 상품이 없으면 마지막으로 본 상품은 널을 반환한다`() =
        runTest {
            val repository = RecentItemRepositoryImpl(MockRecentItemDao(), MockProductRepository(emptyList()))

            val lastViewedItem = repository.getLastViewedItem()

            assertThat(lastViewedItem).isNull()
        }
}
