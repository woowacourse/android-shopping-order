package woowacourse.shopping.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RecentProductsTest {

    lateinit var recentProducts: RecentProducts

    @BeforeEach
    fun setUp() {
        recentProducts = RecentProducts(
            listOf(
                RecentProduct(1, Product(1, "새상품", Price(1000), "url")),
                RecentProduct(2, Product(2, "새상품", Price(1000), "url")),
                RecentProduct(3, Product(3, "새상품", Price(1000), "url"))
            )
        )
    }

    @Test
    fun `만약 현재 보려고 하는 상품이 바로 이전에 보았던 상품이라면 두번째 이전의 상품을 반환한다`() {
        // given
        val recentProduct = recentProducts.values.first()

        // when
        val actual = recentProducts.getLatestProduct(recentProduct.product)

        // then
        val expected = recentProducts.values[1].product

        assertEquals(expected, actual)
    }

    @Test
    fun `만약 현재 보려고 하는 상품이 바로 이전에 보았던 상품이 아니라면 바로 이전 상품을 반환한다`() {
        // given
        val recentProduct = recentProducts.values.last()

        // when
        val actual = recentProducts.getLatestProduct(recentProduct.product)

        // then
        val expected = recentProducts.values.first().product

        assertEquals(expected, actual)
    }
}
