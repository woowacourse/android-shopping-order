package com.woowacourse.shopping.domain.order

import org.junit.Assert.assertEquals
import org.junit.Test
import woowacourse.shopping.domain.order.DiscountPolicy
import woowacourse.shopping.domain.order.Payment

class PaymentTest {
    @Test
    fun 할인이_100원_200원_이면_총_할인은_300원이다() {
        // given
        val payment = Payment(
            listOf(
                DiscountPolicy("", 0.1, 100),
                DiscountPolicy("", 0.1, 200)
            )
        )

        // when
        val actual = payment.calculateDiscountPrice()

        // then
        val expect = 300
        assertEquals(expect, actual)
    }
}
