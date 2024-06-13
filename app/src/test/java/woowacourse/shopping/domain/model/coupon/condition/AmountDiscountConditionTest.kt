package woowacourse.shopping.domain.model.coupon.condition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.cartItemsByTotalPrice
import java.time.LocalDate
import java.time.LocalDateTime

class AmountDiscountConditionTest {
    @Test
    fun `할인 만료 날짜가 3월 1일이고 현재 날짜가 2월 1일이라면 쿠폰을 적용할 수 있다`() {
        // given
        val amountDiscountCondition = AmountDiscountCondition(0)
        val expirationDate = LocalDate.of(2024, 3, 1)
        val currentDate = LocalDateTime.of(2024, 2, 1, 0, 0)
        val cartItems = cartItemsByTotalPrice(1000)

        // when
        val actual = amountDiscountCondition.available(expirationDate, currentDate, cartItems)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `할인 만료 날짜가 3월 1일이고 현재 날짜가 3월 1일이라면 쿠폰을 적용할 수 있다`() {
        // given
        val amountDiscountCondition = AmountDiscountCondition(0)
        val expirationDate = LocalDate.of(2024, 3, 1)
        val currentDate = LocalDateTime.of(2024, 3, 1, 0, 0)
        val cartItems = cartItemsByTotalPrice(1000)

        // when
        val actual = amountDiscountCondition.available(expirationDate, currentDate, cartItems)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `할인 만료 날짜가 3월 1일이고 현재 날짜가 4월 1일이라면 쿠폰을 적용할 수 없다`() {
        // given
        val amountDiscountCondition = AmountDiscountCondition(0)
        val expirationDate = LocalDate.of(2024, 3, 1)
        val currentDate = LocalDateTime.of(2024, 4, 1, 0, 0)
        val cartItems = cartItemsByTotalPrice(1000)

        // when
        val actual = amountDiscountCondition.available(expirationDate, currentDate, cartItems)

        // then
        assertThat(actual).isFalse
    }

    @Test
    fun `최소 구매 금액이 50_000원이고 구매 금액이 60_000원이면 쿠폰을 적용할 수 있다`() {
        // given
        val amountDiscountCondition = AmountDiscountCondition(50_000)
        val cartItems = cartItemsByTotalPrice(60_000)

        // when
        val actual = amountDiscountCondition.available(cartItems = cartItems)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `최소 구매 금액이 50_000원이고 구매 금액이 40_000원이면 쿠폰을 적용할 수 없다`() {
        // given
        val amountDiscountCondition = AmountDiscountCondition(50_000)
        val cartItems = cartItemsByTotalPrice(40_000)

        // when
        val actual = amountDiscountCondition.available(cartItems = cartItems)

        // then
        assertThat(actual).isFalse
    }
}
