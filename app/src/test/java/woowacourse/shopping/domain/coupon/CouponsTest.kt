package woowacourse.shopping.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponsTest {
    @Test
    fun `쿠폰들 중 사용 가능한 쿠폰들을 반환한다`() {
        //given
        val coupons = Coupons(COUPONS)
        val discountableCartItems = listOf(createCartItem(100_000, 3))
        val miracleCouponTime = LocalDateTime.of(2025, 6, 30, 5, 0)

        //when
        val actual = coupons.getAvailable(discountableCartItems, miracleCouponTime)

        //then
        assertThat(actual).isEqualTo(COUPONS)
    }

    @Test
    fun `모든 쿠폰이 사용 불가능하면 emptyList()를 반환한다`() {
        val coupons = Coupons(COUPONS)
        val nonDiscountableCartItems = listOf(createCartItem(100, 1))
        val nonMiracleCouponTime = LocalDateTime.of(2025, 6, 30, 20, 0)

        val actual = coupons.getAvailable(nonDiscountableCartItems, nonMiracleCouponTime)

        assertThat(actual).isEqualTo(emptyList<Coupon>())
    }
}
