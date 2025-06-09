package woowacourse.shopping.domain.product

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fixture.PRODUCT1
import woowacourse.shopping.fixture.RECENT_PRODUCTS
import woowacourse.shopping.fixture.SHOPPING_CARTS1

class RecommendProductsTest {
    @Test
    fun `장바구니에 있는 상품은 추천 상품에 없다`() {
        // given
        //
        val product = RECENT_PRODUCTS
        val shoppingCarts = SHOPPING_CARTS1.shoppingCartItems

        // when
        val actual =
            RecommendProducts(
                product,
                shoppingCarts,
            ).get()

        // then
        assertThat(actual).doesNotContain(PRODUCT1)
    }

    @Test
    fun `가장 최근 본 상품의 카테고리만 추천 상품에 나열된다`() {
        // given
        val product = RECENT_PRODUCTS
        val shoppingCarts = SHOPPING_CARTS1.shoppingCartItems

        // when
        val actual =
            RecommendProducts(
                product,
                shoppingCarts,
            ).get()

        // then
        assertThat(actual).allMatch {
            it.category == RECENT_PRODUCTS.first().category
        }
    }
}
