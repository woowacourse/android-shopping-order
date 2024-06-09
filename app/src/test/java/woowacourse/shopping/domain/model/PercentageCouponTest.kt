package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.availableTime
import woowacourse.shopping.cartItemsBySize
import woowacourse.shopping.cartItemsByTotalPrice
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PercentageCouponTest {
    @Test
    fun `4시부터 7시까지 쿠폰을 적용할 수 있고, 현재 시간이 11시라면 쿠폰을 적용할 수 없다`() {
        // given
        val percentageCoupon =
            percentageCoupon(
                expirationDate = LocalDate.of(2024, 3, 1),
                availableTime = availableTime(4, 7),
            )
        val cartItems = cartItemsBySize(1)

        // when
        val actual = percentageCoupon.available(cartItems, LocalDateTime.of(2024, 3, 1, 11, 0))

        // then
        assertThat(actual).isFalse
    }

    @Test
    fun `4시부터 7시까지 쿠폰을 적용할 수 있고, 현재 시간이 4시라면 쿠폰을 적용할 수 있다`() {
        // given
        val percentageCoupon =
            percentageCoupon(
                expirationDate = LocalDate.of(2024, 3, 1),
                availableTime = availableTime(4, 7),
            )
        val cartItems = cartItemsBySize(1)

        // when
        val actual = percentageCoupon.available(cartItems, LocalDateTime.of(2024, 3, 1, 4, 0))

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `할인율이 50%이고 주문 금액이 40,000원라면 할인 금액은 20,000원이다`() {
        // given
        val percentageCoupon = percentageCoupon(discount = 50)
        val cartItems = cartItemsByTotalPrice(40_000)

        // when
        val actual = percentageCoupon.discountPrice(cartItems)

        // then
        assertThat(actual).isEqualTo(20_000)
    }

    private fun percentageCoupon(
        id: Int = 0,
        expirationDate: LocalDate = LocalDate.of(3000, 10, 10),
        discount: Int = 100,
        availableTime: AvailableTime =
            AvailableTime(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59)),
    ): PercentageCoupon {
        return PercentageCoupon(id, "", "", expirationDate, discount, availableTime)
    }
}
