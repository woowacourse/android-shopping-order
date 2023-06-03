package com.woowacourse.shopping.domain.cart

import org.junit.Assert.assertEquals
import org.junit.Test
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product

class CartTest {
    @Test
    fun 장바구니에_상품이_2개_2000원_3개_1000원이면_총합은_7000원이다() {
        // given
        val cart = Cart(
            setOf(
                CartItem(
                    0, quantity = 2, Product(0, "", price = 2000, "")
                ),
                CartItem(
                    0, quantity = 3, Product(0, "", price = 1000, "")
                ),
            )
        )

        // when
        val actual = cart.calculateTotalPrice()

        // then
        val expect = 7000
        assertEquals(expect, actual)
    }
}
