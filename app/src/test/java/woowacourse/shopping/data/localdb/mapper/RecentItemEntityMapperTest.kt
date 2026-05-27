package woowacourse.shopping.data.localdb.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.localdb.entity.RecentItemEntity
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName
import woowacourse.shopping.model.RecentItem

class RecentItemEntityMapperTest {
    @Test
    fun `최근 본 상품 엔티티를 도메인 상품으로 변환한다`() {
        val entity =
            RecentItemEntity(
                productId = 1,
                name = "상품",
                imageUrl = "",
                timestamp = 100L,
            )
        val recentProduct =
            RecentItem(
                productId = 1,
                name = "상품",
                imageUrl = "",
            )
        val result = entity.toDomain()
        assertThat(result).isEqualTo(recentProduct)
    }

    @Test
    fun `도메인 상품을 최근 본 상품 엔티티로 변환한다`() {
        val product =
            Product(
                id = 1,
                name = ProductName("상품"),
                price = Money(2000),
                imageUrl = "1",
                category = "book",
            )

        val entity = product.toRecentItemEntity(timestamp = 100L)

        assertThat(entity.productId).isEqualTo(product.id)
        assertThat(entity.timestamp).isEqualTo(100L)
    }
}
