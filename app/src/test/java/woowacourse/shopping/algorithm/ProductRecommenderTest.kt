package woowacourse.shopping.algorithm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.recommender.ProductRecommender

class ProductRecommenderTest {
    private val electronics = "electronics"
    private val food = "food"

    private fun createProduct(
        id: Long,
        name: String,
        category: String,
    ) = Product(
        id = id,
        name = name,
        price = Money(1000),
        imageUrl = "url",
        category = category,
    )

    @Test
    fun `마지막으로 본 상품과 같은 카테고리의 상품들을 추천한다`() {
        // given
        val lastViewed = createProduct(1, "TV", electronics)
        val p2 = createProduct(2, "Radio", electronics)
        val p3 = createProduct(3, "Apple", food)

        val allProducts = listOf(lastViewed, p2, p3)
        val cartItems = emptyList<CartItem>()

        // when
        val recommended = ProductRecommender.recommendProduct(lastViewed, allProducts, cartItems)

        // then
        // Current implementation: includes lastViewed if it's in allProducts and same category
        assertEquals(2, recommended.size)
        assertTrue(recommended.contains(lastViewed))
        assertTrue(recommended.contains(p2))
    }

    @Test
    fun `같은 카테고리 상품이 없으면 전체 상품 중에서 추천한다`() {
        // given
        val lastViewed = createProduct(1, "TV", electronics)
        val p2 = createProduct(2, "Apple", food)
        val p3 = createProduct(3, "Banana", food)

        val allProducts = listOf(p2, p3) // electronics category is empty
        val cartItems = emptyList<CartItem>()

        // when
        val recommended = ProductRecommender.recommendProduct(lastViewed, allProducts, cartItems)

        // then
        assertEquals(2, recommended.size)
        assertTrue(recommended.contains(p2))
        assertTrue(recommended.contains(p3))
    }

    @Test
    fun `장바구니에 담긴 상품은 추천에서 제외한다`() {
        // given
        val lastViewed = createProduct(1, "TV", electronics)
        val p2 = createProduct(2, "Radio", electronics)
        val p3 = createProduct(3, "Laptop", electronics)

        val allProducts = listOf(lastViewed, p2, p3)
        val cartItems = listOf(CartItem(product = p2, quantity = 1))

        // when
        val recommended = ProductRecommender.recommendProduct(lastViewed, allProducts, cartItems)

        // then
        assertTrue(!recommended.contains(p2))
        assertTrue(recommended.contains(lastViewed))
        assertTrue(recommended.contains(p3))
    }

    @Test
    fun `추천 상품은 최대 10개까지만 반환한다`() {
        // given
        val lastViewed = createProduct(0, "Base", electronics)
        val allProducts = (0..14).map { createProduct(it.toLong(), "Product $it", electronics) }
        val cartItems = emptyList<CartItem>()

        // when
        val recommended = ProductRecommender.recommendProduct(lastViewed, allProducts, cartItems)

        // then
        assertEquals(10, recommended.size)
    }
}
