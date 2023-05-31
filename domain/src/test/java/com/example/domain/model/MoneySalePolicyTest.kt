package com.example.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MoneySalePolicyTest {
    @Test
    fun `saleApply_구입 금액이 3만원 미만이라면_할인되지 않는다`() {
        // given 총 27000원의 장바구니 상품들이 준비되어 있다
        val moneySalePolicy = MoneySalePolicy()
        val cartProducts = listOf(
            CartProduct(1L, product(1L, 2000), 1, true),
            CartProduct(2L, product(2L, 5000), 2, true),
            CartProduct(3L, product(3L, 5000), 3, true),
        )

        // when
        val actual = moneySalePolicy.saleApply(cartProducts)

        // then
        val expected = Price(2000 * 1 + 5000 * 2 + 5000 * 3)
        assertEquals(expected, actual)
    }

    @Test
    fun `saleApply_구입 금액이 3만원 이상이면_3000원 할인 된다`() {
        // given 총 30000원의 장바구니 상품들이 준비되어 있다
        val moneySalePolicy = MoneySalePolicy()
        val cartProducts = listOf(
            CartProduct(1L, product(1L, 2000), 1, true),
            CartProduct(2L, product(2L, 5000), 2, true),
            CartProduct(3L, product(3L, 6000), 3, true),
        )

        // when
        val actual = moneySalePolicy.saleApply(cartProducts)

        // then
        val expected = Price((2000 * 1 + 5000 * 2 + 6000 * 3) - 3000)
        assertEquals(expected, actual)
    }

    @Test
    fun `saleApply_구입 금액이 5만원 이상이면_3000원 할인 된다`() {
        // given 총 54000원의 장바구니 상품들이 준비되어 있다
        val moneySalePolicy = MoneySalePolicy()
        val cartProducts = listOf(
            CartProduct(1L, product(1L, 20000), 1, true),
            CartProduct(2L, product(2L, 5000), 2, true),
            CartProduct(3L, product(3L, 6000), 4, true),
        )

        // when
        val actual = moneySalePolicy.saleApply(cartProducts)

        // then
        val expected = Price((20000 * 1 + 5000 * 2 + 6000 * 4) - 5000)
        assertEquals(expected, actual)
    }

    private fun product(productId: Long, price: Int): Product {
        return Product(productId, "", "", Price(price))
    }
}
