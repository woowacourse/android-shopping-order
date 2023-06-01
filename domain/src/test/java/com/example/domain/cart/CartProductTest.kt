package com.example.domain.cart

import com.example.domain.Product
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CartProductTest {
    @Test
    fun `체크를 true로 변경한다`() {
        // given
        val product = Product(1L, "치킨", 13000, "")
        val cartProduct = CartProduct(1L, product, 3, false)
        val expected = true

        // when
        val actual = cartProduct.updateChecked(true)

        // then
        assertThat(expected).isEqualTo(actual.checked)
    }

    @Test
    fun `개수를 1 증가시킨다`() {
        // given
        val product = Product(1L, "치킨", 13000, "")
        val cartProduct = CartProduct(1L, product, 3, false)
        val expected = 4

        // when
        val actual = cartProduct.increaseCount()

        // then
        assertThat(expected).isEqualTo(actual.count)
    }

    @Test
    fun `개수를 1 감소시킨다`() {
        // given
        val product = Product(1L, "치킨", 13000, "")
        val cartProduct = CartProduct(1L, product, 3, false)
        val expected = 2

        // when
        val actual = cartProduct.decreaseCount()

        // then
        assertThat(expected).isEqualTo(actual.count)
    }
}
