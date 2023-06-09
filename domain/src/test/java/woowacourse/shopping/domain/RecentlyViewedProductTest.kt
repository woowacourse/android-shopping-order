package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class RecentlyViewedProductTest {

    @Test
    fun `최근 본 상품은 아이디가 같으면 같다고 판단한다`() {
        val product = Product(1L, "imageUrl", "name", 10000)
        val oneRecentlyViewedProduct = RecentlyViewedProduct(1L, product, LocalDateTime.MAX)
        val otherRecentlyViewedProduct = RecentlyViewedProduct(1L, product, LocalDateTime.MIN)

        assertThat(oneRecentlyViewedProduct).isEqualTo(otherRecentlyViewedProduct)
    }

    @Test
    fun `최근 본 상품은 아이디가 다르면 다르다고 판단한다`() {
        val product = Product(1L, "imageUrl", "name", 10000)
        val oneRecentlyViewedProduct = RecentlyViewedProduct(1L, product, LocalDateTime.MAX)
        val otherRecentlyViewedProduct = RecentlyViewedProduct(2L, product, LocalDateTime.MAX)

        assertThat(oneRecentlyViewedProduct).isNotEqualTo(otherRecentlyViewedProduct)
    }
}
