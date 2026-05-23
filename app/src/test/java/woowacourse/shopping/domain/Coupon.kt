package woowacourse.shopping.domain

import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.OrderContext

class BogoCoupon(
    override val validUntil: LocalDate,
) : Coupon {
    override fun isApplicable(context: OrderContext): Boolean {
        TODO("Not yet implemented")
    }

    override fun discountAmount(context: OrderContext): Int {
        TODO("Not yet implemented")
    }
}

class CouponTest {
    @Test
    fun `FixedDiscountCoupon은 최소 주문 금액 미만이면 적용 불가다`() {
        // given: 주문 금액이 50000원이고 쿠폰이 주어진다.
        val price = 50_000
        val coupon = FixedDiscountCoupon(validUntil = LocalDate.of(2026, 11, 30))

        // when: 할인 금액을 계산할 때
        val discountedPrice = price - coupon.discountAmount(OrderContext(totalPrice = price))

        // then: 할인이 적용되지 않는다
        assertThat(discountedPrice).isEqualTo(50_000)
    }

    @Test
    fun `FixedDiscountCoupon은 최소 주문 금액 이상이면 5,000원을 할인한다`() {
        // given: 주문 금액이 100_000원이고 쿠폰이 주어진다.
        val price = 100_000
        val coupon = FixedDiscountCoupon(validUntil = LocalDate.of(2026, 11, 30))

        // when: 할인 금액을 계산할 때
        val discountedPrice = price - coupon.discountAmount(OrderContext(totalPrice = price))

        // then: 할인이 적용되지 않는다
        assertThat(discountedPrice).isEqualTo(95_000)
    }

    @Test
    fun `BogoCoupon은 장바구니에 동일 상품이 3개 이상 있어야 적용 가능하다`() {
    }

    @Test
    fun `BogoCoupon은 3개 이상 담긴 상품이 여러 개일 때 가장 비싼 상품 1개 가격을 할인한다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `BogoCoupon은 3개 이상인 상품이 없으면 적용 불가다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `FreeShippingCoupon은 최소 주문 금액(50,000원) 미만이면 적용 불가다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `FreeShippingCoupon은 적용 시 배송비를 0원으로 만든다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `FREESHIPPING 쿠폰 적용 시 배송비는 0원이다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `MiracleSaleCoupon은 04시부터 07시 시간대가 아니면 적용 불가다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `MiracleSaleCoupon은 적용 시 총 상품 금액의 30퍼센트를 할인한다`() {
        // given:

        // when:

        // then:
    }

    @Test
    fun `쿠폰은 만료일을 초과하면 사용할 수 없다`() {
        // given:

        // when:

        // then:
    }
}
