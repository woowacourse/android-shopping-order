package woowacourse.shopping.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.fixture.CartProductFixture
import woowacourse.shopping.model.fixture.ProductFixture

class CartProductsTest {
    @Test
    fun `상품들을 추가할 수 있다`() {
        // given
        val cartProducts = CartProducts()
        val cartProductItems = CartProductFixture.getCartProducts(quantity = 1, 1, 2, 3)

        // when
        cartProducts.addProducts(cartProductItems)

        // then
        assertThat(cartProducts.items).isEqualTo(cartProductItems)
    }

    @Test
    fun `2번 상품이 2개일 때 상품의 개수를 하나 줄이면 1개가 된다`() {
        // given
        val cartProductItems = CartProductFixture.getCartProducts(quantity = 2, 1, 2)

        val cartProducts = CartProducts(cartProductItems)

        // when
        cartProducts.subProductByCount(
            product = ProductFixture.getProduct(2),
            count = 1,
        )

        // then
        val expected = CartProductFixture.getCartProducts(quantity = 2, 1) +
            CartProductFixture.getCartProduct(id = 2, quantity = 1)

        assertThat(cartProducts.items).isEqualTo(expected)
    }

    @Test
    fun `1번 상품이 1개일 때 상품의 개수를 줄일 수 없다`() {
        // given
        val cartProductItems = CartProductFixture.getCartProducts(quantity = 1, 1, 2)

        val cartProducts = CartProducts(cartProductItems)

        // when
        cartProducts.subProductByCount(
            product = ProductFixture.getProduct(1),
            count = 1,
        )

        // then
        val expected = CartProductFixture.getCartProducts(quantity = 1, 1, 2)

        assertThat(cartProducts.items).isEqualTo(expected)
    }

    @Test
    fun `2번 상품이 2개일 때 상품의 개수를 하나 추가하면 3개가 된다`() {
        // given
        val productItems = CartProductFixture.getCartProducts(quantity = 2, 1, 2)
        val cartProducts = CartProducts(productItems)

        // when
        cartProducts.addProductByCount(
            product = ProductFixture.getProduct(2),
            count = 1,
        )

        // then
        val expected = CartProductFixture.getCartProducts(quantity = 2, 1) +
            CartProductFixture.getCartProduct(id = 2, quantity = 3)

        assertThat(cartProducts.items).isEqualTo(expected)
    }
}
