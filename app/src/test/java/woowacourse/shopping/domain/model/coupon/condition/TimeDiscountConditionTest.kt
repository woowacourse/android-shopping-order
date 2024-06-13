package woowacourse.shopping.domain.model.coupon.condition

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.availableTime
import woowacourse.shopping.cartItemsBySize
import java.time.LocalDateTime

class TimeDiscountConditionTest {
    @Test
    fun `할인 적용 시간이 4시부터 7시이고 현재 날짜가 4시 30분이라면 쿠폰을 적용할 수 있다`() {
        // given
        val timeDiscountCondition = TimeDiscountCondition(availableTime(4, 7))
        val cartItems = cartItemsBySize(1)

        // when
        val actual =
            timeDiscountCondition.available(
                currentDateTime = LocalDateTime.of(2024, 4, 30, 4, 30),
                cartItems = cartItems,
            )

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `할인 적용 시간이 4시부터 7시이고 현재 날짜가 8시라면 쿠폰을 적용할 수 없다`() {
        // given
        val timeDiscountCondition = TimeDiscountCondition(availableTime(4, 7))
        val cartItems = cartItemsBySize(1)

        // when
        val actual =
            timeDiscountCondition.available(
                currentDateTime = LocalDateTime.of(2024, 4, 30, 8, 0),
                cartItems = cartItems,
            )

        // then
        assertThat(actual).isFalse
    }
}
