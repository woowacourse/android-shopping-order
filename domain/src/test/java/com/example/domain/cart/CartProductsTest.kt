package com.example.domain.cart

import com.example.domain.Product
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CartProductsTest {
    @Test
    fun `장바구니 아이템 하나의 checked를 true로 변경한다`() {
        // given
        val product = Product(1L, "치킨", 13000)
        val cartProduct1 = CartProduct(1L, product, 3, false)
        val cartProduct2 = CartProduct(1L, product, 3, true)

        val cartProductList = mutableListOf(cartProduct1)
        val cartProducts = CartProducts(cartProductList)

        val actual = true

        // when
        cartProducts.updateCheckedBy(cartProduct1.id, true)

        val expected = cartProducts.getOrNull(cartProduct2.id)?.checked

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `장바구니 아이템을 삭제한다`() {
        // given
        val product = Product(1L, "치킨", 13000)
        val cartProduct = CartProduct(1L, product, 3, false)

        val cartProductList = mutableListOf(cartProduct)
        val cartProducts = CartProducts(cartProductList)

        // when
        cartProducts.remove(cartProduct.id)
        val actual = cartProducts.getOrNull(cartProduct.id)

        // then
        assertThat(actual).isNull()
    }

    @Test
    fun `장바구니 전체 아이템의 checked를 true로 변경한다`() {
        // given
        val cartProducts = getCartProducts()

        // when
        val actual = cartProducts.updateCheckedAll(true)

        // then
        assertThat(actual.all.all { it.checked }).isTrue
    }

    @Test
    fun `장바구니 첫 번째 아이템의 count를 2로 변경한다`() {
        // given
        val cartProducts = getCartProducts()

        // when
        val actual = cartProducts.changeCount(0L, 2)

        // then
        assertThat(actual.all[0].count).isEqualTo(2)
    }

    @Test
    fun `장바구니 아이템 중 넘겨준 id에 해당하는 아이템의 checked를 true로 변경한다`() {
        // given
        val cartProducts = getCartProducts()
        val updateCartIds = listOf(0L, 1L)

        // when
        val actual = cartProducts.updateAllCheckedBy(updateCartIds, true)

        // then
        assertThat(actual.all[0].checked).isTrue
        assertThat(actual.all[1].checked).isTrue
        assertThat(actual.all[2].checked).isFalse
    }

    companion object {
        fun getCartProducts(): CartProducts {
            return CartProducts(
                List(6) {
                    Product(1L, "치킨", 13000)
                }.mapIndexed { index, product ->
                    CartProduct(index.toLong(), product, index + 1, false)
                }.toMutableList(),
            )
        }
    }
}
