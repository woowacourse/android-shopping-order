package woowacouse.shopping.model.recentproduct

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacouse.shopping.model.product.Product

class RecentProductsTest {
    @Test
    fun `최근 본 상품 아이디가 1인 아이템을 조회할 수 있다`() {
        val recentProducts = RecentProducts(
            listOf(
                RecentProduct(1L, 1L, "피자", 12_000, 1),
                RecentProduct(2L, 2L, "치킨", 15_000, 1),
            )
        )

        val actual = recentProducts.getRecentProduct(1L)
        val expected =
            RecentProduct(1L, 1L, "피자", 12_000, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun `최근 본 상품 목록 아이템들을 조회할 수 있다`() {
        val recentProducts = RecentProducts(
            listOf(
                RecentProduct(1L, 1L, "피자", 12_000, 1),
                RecentProduct(2L, 2L, "치킨", 15_000, 1),
            )
        )

        val actual = recentProducts.getAll()
        val expected = listOf(
            RecentProduct(1L, 1L, "피자", 12_000, 1),
            RecentProduct(2L, 2L, "치킨", 15_000, 1),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `최근 본 상품 아이템을 추가할 수 있다`() {
        // given
        val recentProducts = RecentProducts(
            listOf(
                RecentProduct(1L, 1L, "피자", 12_000, 1),
            )
        )

        // when
        val actual = recentProducts.addRecentProduct(
            RecentProduct(2L, 2L, "치킨", 15_000, 1)
        ).getAll()

        val expected = listOf(
            RecentProduct(1L, 1L, "피자", 12_000, 1),
            RecentProduct(2L, 2L, "치킨", 15_000, 1),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `최근 본 상품 아이디로 아이템을 삭제할 수 있다`() {
        // given
        val recentProducts = RecentProducts(
            listOf(
                RecentProduct(1L, 1L, "피자", 12_000, 1),
                RecentProduct(2L, 2L, "치킨", 15_000, 1),
            )
        )

        // when
        val actual = recentProducts.deleteRecentProduct(2L).getAll()

        val expected = listOf(
            RecentProduct(1L, 1L, "피자", 12_000, 1),
        )
        assertEquals(expected, actual)
    }

    companion object {
        fun RecentProduct(id: Long, productId: Long, productName: String, price: Int, count: Int): RecentProduct =
            RecentProduct(id, Product(productId, productName, price, count))
    }
}
