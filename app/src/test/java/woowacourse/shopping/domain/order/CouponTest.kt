package woowacourse.shopping.domain.order

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fixture.TestFixtures.PRODUCTS_TO_ORDER_BONUS_COUPON
import java.time.LocalDate
import java.time.LocalTime

class CouponTest {
    @Test
    fun `PriceDiscount 쿠폰은 최소 주문 금액 이상일 때 사용 가능하고 할인 금액이 음수로 계산된다`() {
        // given:
        val coupon =
            Coupon.PriceDiscount(
                id = 1,
                code = "DISCOUNT_10_000",
                description = "5만원 구매 시 10,000원 할인 쿠폰",
                expirationDate = LocalDate.now().plusDays(1),
                discount = 10_000,
                minimumAmount = 50_000,
            )

        // when:
        val actualDiscountPrice: Int = coupon.calculateDiscount()
        val actualAvailable: Boolean = coupon.isAvailable(50_000)

        // then:
        assertThat(actualAvailable).isEqualTo(true)
        assertThat(actualDiscountPrice).isEqualTo(-10_000)
    }

    @Test
    fun `Bonus 쿠폰은 구매할 개수 + 상품 얻는 개수 이상일 경우 사용 가능하고 가장 비싼 상품 가격만큼 할인된다`() {
        // given:
        val coupon =
            Coupon.Bonus(
                id = 2,
                code = "2+1BONUS",
                description = "2+1 쿠폰",
                expirationDate = LocalDate.now().plusDays(1),
                buyQuantity = 2,
                getQuantity = 1,
            )

        val products = PRODUCTS_TO_ORDER_BONUS_COUPON

        // when:
        val actualAvailable = coupon.isAvailable(products)
        val actualDiscountPrice = coupon.calculateDiscount(products)

        // then:
        assertThat(actualAvailable).isEqualTo(true)
        assertThat(actualDiscountPrice).isEqualTo(-100_000)
    }

    @Test
    fun `FreeShipping 쿠폰은 최소 주문 금액 이상일 때 사용 가능하고 배송비만큼 할인된다`() {
        // given:
        val coupon =
            Coupon.FreeShipping(
                id = 3,
                code = "FREESHIP",
                description = "무료배송 쿠폰",
                expirationDate = LocalDate.now().plusDays(1),
                minimumAmount = 20_000,
            )

        val shippingFee = ShippingFee(amount = 3_000)

        // when:
        val actualAvailable = coupon.isAvailable(20_000)
        val actualDiscountPrice = coupon.calculateDiscount(shippingFee)

        // then:
        assertThat(actualAvailable).isEqualTo(true)
        assertThat(actualDiscountPrice).isEqualTo(-3_000)
    }

    @Test
    fun `PercentageDiscount 쿠폰은 시간 범위 내에 있을 때 사용 가능하고 퍼센트로 할인된다`() {
        // given:
        val now = LocalTime.of(9, 30)
        val coupon =
            Coupon.PercentageDiscount(
                id = 4,
                code = "PERCENT20",
                description = "20% 할인 쿠폰",
                expirationDate = LocalDate.now().plusDays(1),
                discountPercentage = 20,
                availableStartTime = LocalTime.of(9, 0),
                availableEndTime = LocalTime.of(18, 0),
            )

        // when:
        val actualAvailable = coupon.isAvailable(now)
        val actualDiscountPrice = coupon.calculateDiscount(10_000)

        // then:
        assertThat(actualAvailable).isEqualTo(true)
        assertThat(actualDiscountPrice).isEqualTo(-2_000)
    }

    @Test
    fun `만료된 쿠폰은 isExpiration이 true를 반환한다`() {
        // given:
        val coupon =
            Coupon.PriceDiscount(
                id = 5,
                code = "EXPIRED",
                description = "만료된 쿠폰",
                expirationDate = LocalDate.now().minusDays(1),
                discount = 5_000,
                minimumAmount = 10_000,
            )

        // when:
        val isExpired = coupon.isExpiration

        // then:
        assertThat(isExpired).isEqualTo(true)
    }
}
