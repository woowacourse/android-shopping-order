package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class CartTest {
    private val product1 = Product(1L, "", "", Price(1000), "")
    private val product2 = Product(2L, "", "", Price(1000), "")
    private val product3 = Product(3L, "", "", Price(1000), "")

    private val cartProduct1 = CartProduct(cartId = 101L, product = product1, quantity = 2)
    private val cartProduct2 = CartProduct(cartId = 102L, product = product2, quantity = 1)
    private val cartProduct3 = CartProduct(cartId = 103L, product = product3, quantity = 5)

    @Test
    fun `카트에 여러 상품을 추가하면 모두 저장된다`() {
        // Give
        val cart = Cart()

        // When
        cart.addAll(listOf(cartProduct1, cartProduct2))

        // Then
        assertAll(
            { assertThat(cart.findQuantityByProductId(product1.id)).isEqualTo(2) },
            { assertThat(cart.findQuantityByProductId(product2.id)).isEqualTo(1) },
        )
    }

    @Test
    fun `카트에 단일 상품을 추가하면 해당 상품이 저장된다`() {
        // Give
        val cart = Cart()

        // When
        cart.addCartProductToCart(cartProduct1)
        val actual = cart.findCartProductByProductId(product1.id)

        // Then
        assertAll(
            { assertThat(actual).isNotNull() },
            { assertThat(actual?.cartId).isEqualTo(cartProduct1.cartId) },
            { assertThat(actual?.quantity).isEqualTo(cartProduct1.quantity) },
        )
    }

    @Test
    fun `상품 ID로 카트에서 상품을 찾을 수 있다`() {
        // Give
        val cart = Cart(listOf(cartProduct1, cartProduct2))

        // When
        val found = cart.findCartProductByProductId(product2.id)

        // Then
        assertAll(
            { assertThat(found).isNotNull() },
            { assertThat(found).isEqualTo(cartProduct2) },
        )
    }

    @Test
    fun `상품 ID로 수량을 조회할 수 있다`() {
        // Give
        val cart = Cart(listOf(cartProduct1))

        // When
        val actual = cart.findQuantityByProductId(product1.id)

        // Then
        assertThat(actual).isEqualTo(cartProduct1.quantity)
    }

    @Test
    fun `존재하지 않는 상품의 수량을 조회하면 0이 반환된다`() {
        // Give
        val cart = Cart(listOf(cartProduct1))

        // When
        val actual = cart.findQuantityByProductId(999L)

        // Then
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `상품 ID로 카트 ID를 조회할 수 있다`() {
        // Give
        val cart = Cart(listOf(cartProduct2))

        // When
        val actual = cart.findCartIdByProductId(product2.id)

        // Then
        assertThat(actual).isEqualTo(102L)
    }

    @Test
    fun `존재하지 않는 상품의 카트 ID를 조회하면 예외가 발생한다`() {
        // Give
        val cart = Cart()

        // Then
        assertThrows<IllegalArgumentException> {
            cart.findCartIdByProductId(999L)
        }
    }

    @Test
    fun `상품의 수량을 업데이트할 수 있다`() {
        // Give
        val cart = Cart(listOf(cartProduct1))

        // When
        cart.updateQuantityByProductId(product1.id, 10)

        // Then
        val updated = cart.findCartProductByProductId(product1.id)
        assertAll(
            { assertThat(updated).isNotNull() },
            { assertThat(updated?.quantity).isEqualTo(10) },
        )
    }

    @Test
    fun `카트 ID로 상품을 삭제할 수 있다`() {
        // Give
        val cart = Cart(listOf(cartProduct1, cartProduct2, cartProduct3))

        // When
        cart.deleteCartProductFromCartByCartId(102L)

        // Then
        assertAll(
            { assertThat(cart.findCartProductByProductId(product2.id)).isNull() },
            { assertThat(cart.findCartProductByProductId(product1.id)).isNotNull() },
            { assertThat(cart.findCartProductByProductId(product3.id)).isNotNull() },
        )
    }
}
