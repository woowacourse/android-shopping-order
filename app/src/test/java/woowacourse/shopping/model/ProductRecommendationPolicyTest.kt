@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductRecommendationPolicyTest {
    private val policy = ProductRecommendationPolicy()

    @Test
    fun `제외할 상품 개수만큼 조회 개수를 늘린다`() {
        val actual = policy.calculateFetchSize(setOf(1L, 2L, 3L))

        assertEquals(13, actual)
    }

    @Test
    fun `제외할 상품을 빼고 최대 10개까지만 추천한다`() {
        val products = (1L..12L).map { product(it) }

        val actual =
            policy.recommend(
                products = products,
                excludedProductIds = setOf(1L, 2L),
            )

        assertEquals((3L..12L).toList(), actual.map { it.id })
    }

    @Test
    fun `추천 가능한 상품이 10개 미만이면 가능한 개수만 반환한다`() {
        val products = listOf(product(1L), product(2L), product(3L))

        val actual =
            policy.recommend(
                products = products,
                excludedProductIds = setOf(1L),
            )

        assertEquals(listOf(2L, 3L), actual.map { it.id })
    }

    private fun product(id: Long): Product =
        Product(
            id = id,
            name = "상품$id",
            price = Money((10_000 + id).toInt()),
            imageUrl = "https://example.com/product-$id.png",
            category = "dessert",
        )
}
