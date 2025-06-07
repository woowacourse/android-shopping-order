package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AvailableCouponsTest {
    @Test
    fun `쿠폰들 중 사용 가능한 쿠폰들을 반환한다`() {
        val availableCoupons = AvailableCoupons(
            COUPONS,
            listOf(createCartItem(100_000,3)),
            LocalDateTime.of(2025,6,30,5,0)
        )

        val actual = availableCoupons.get()

        assertThat(actual).isEqualTo(COUPONS)
    }


    @Test
    fun `모든 쿠폰이 사용 불가능하면 emptyList()를 반환한다`() {
        val availableCoupons = AvailableCoupons(
            COUPONS,
            listOf(createCartItem(100,1)),
            LocalDateTime.of(2025,6,30,20,0)
        )

        val actual = availableCoupons.get()

        assertThat(actual).isEqualTo(emptyList<Coupon>())
    }
}
