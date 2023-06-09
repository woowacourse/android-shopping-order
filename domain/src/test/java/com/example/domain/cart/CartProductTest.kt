package com.example.domain.cart

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CartProductTest {

    @Test
    fun `장바구니 제품의 개수를 변경할 수 있다`() {
        // given
        val cartProduct: CartProduct = createCartProduct(quantity = 1)
        val quantity: Int = 10

        // when
        cartProduct.quantity = quantity

        // then
        assertThat(cartProduct.quantity).isEqualTo(quantity)
    }

    @Test
    fun `장바구니 제품의 개수를 변경할 때 최소 개수 1보다 작다면 장바구니 제품의 개수를 1로 가진다`() {
        // given
        val cartProduct: CartProduct = createCartProduct()

        // when
        cartProduct.quantity = 0

        // then
        assertThat(cartProduct.quantity).isEqualTo(1)
    }

    @Test
    fun `장바구니 제품의 개수를 변경할 때 최대 개수 99보다 크다면 장바구니 제품의 개수를 99로 가진다`() {
        // given
        val cartProduct: CartProduct = createCartProduct()

        // when
        cartProduct.quantity = 100

        // then
        assertThat(cartProduct.quantity).isEqualTo(99)
    }
}
