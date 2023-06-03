package com.woowacourse.shopping.domain.cart

import org.junit.Assert.assertEquals
import org.junit.Test
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product

class CartItemTest {
    @Test
    fun 상품의_수량이_2개이고_가격이_2000원이면_총_가격은_4000원이다() {
        // given
        val cartItem = CartItem(0, quantity = 2, Product(0, "", price = 2000, ""))

        // when
        val actual = cartItem.calculateOrderPrice()

        // then
        val expect = 4000
        assertEquals(expect, actual)
    }

    @Test
    fun 상품의_수량이_2개이고_1개를_더하면_3개가_된다() {
        // given
        val cartItem = CartItem(0, quantity = 2, Product(0, "", 0, ""))

        // when
        val actual = cartItem.plusQuantity()

        // then
        val expect = CartItem(0, quantity = 3, Product(0, "", 0, ""))
        assertEquals(expect, actual)
    }

    @Test
    fun 상품의_수량이_2개이고_1개를_빼면_1개가_된다() {
        // given
        val cartItem = CartItem(0, quantity = 2, Product(0, "", 0, ""))

        // when
        val actual = cartItem.minusQuantity()

        // then
        val expect = CartItem(0, quantity = 1, Product(0, "", 0, ""))
        assertEquals(expect, actual)
    }
}
