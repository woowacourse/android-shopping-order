package woowacourse.shopping.domain

import java.time.LocalDate
import java.time.LocalTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.coupon.BogoCoupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleMorningCoupon
import woowacourse.shopping.domain.coupon.OrderContext
import woowacourse.shopping.fixture.TestCartContentFixture
import woowacourse.shopping.fixture.TestProductFixture

class CouponTest {
    @Test
    fun `FixedDiscountCoupon은 최소 주문 금액 미만이면 적용 불가다`() {
        // given: 주문 금액이 50000원이고 쿠폰이 주어진다.
        val price = 50_000
        val coupon = FixedDiscountCoupon(expirationDate = LocalDate.of(2026, 11, 30))

        // when: 할인 금액을 계산할 때
        val discountedPrice = price - coupon.discountAmount(OrderContext(totalPrice = price))

        // then: 할인이 적용되지 않는다
        assertThat(discountedPrice).isEqualTo(50_000)
    }

    @Test
    fun `FixedDiscountCoupon은 최소 주문 금액 이상이면 5,000원을 할인한다`() {
        // given: 주문 금액이 100_000원이고 쿠폰이 주어진다.
        val price = 100_000
        val coupon = FixedDiscountCoupon(expirationDate = LocalDate.of(2026, 11, 30))

        // when: 할인 금액을 계산할 때
        val discountedPrice = price - coupon.discountAmount(OrderContext(totalPrice = price))

        // then: 할인이 적용되지 않는다
        assertThat(discountedPrice).isEqualTo(95_000)
    }

    @Test
    fun `BogoCoupon은 장바구니에 동일 상품이 3개 이상 있어야 적용 가능하다`() {
        // given: 장바구니에 동일한 상품이 3개 존재하고 쿠폰이 주어진다
        val cartContent = TestCartContentFixture.cartContent(quantity = 3)
        val cart = Cart(
            cartContents = listOf(cartContent),
        )
        val coupon = BogoCoupon(expirationDate = LocalDate.of(2026, 11, 30))
        val price = cart.cartContents.sumOf { it.quantity * it.product.priceAmount() }

        // when: 할인 금액을 계산할 때
        val applicable = coupon.isApplicable(OrderContext(items = cart.cartContents))

        // then: 할인을 적용할 수 있다
        assertThat(applicable).isEqualTo(true)
    }

    @Test
    fun `BogoCoupon은 3개 이상인 상품이 없으면 적용 불가다`() {
        // given: 장바구니에 동일한 상품이 2개 존재하고 쿠폰이 주어진다
        val cartContent = TestCartContentFixture.cartContent(quantity = 2)
        val cart = Cart(
            cartContents = listOf(cartContent),
        )
        val coupon = BogoCoupon(expirationDate = LocalDate.of(2026, 11, 30))
        val price = cart.cartContents.sumOf { it.quantity * it.product.priceAmount() }

        // when: 할인 금액을 계산할 때
        val applicable = coupon.isApplicable(OrderContext(items = cart.cartContents))

        // then: 할인을 적용할 수 없다
        assertThat(applicable).isEqualTo(false)
    }

    @Test
    fun `BogoCoupon은 3개 이상 담긴 상품이 여러 개일 때 가장 비싼 상품 1개 가격을 할인한다`() {
        // given: 장바구니에 동일한 상품이 3개 존재하고 쿠폰이 주어진다
        val cartContent = TestCartContentFixture.cartContent(quantity = 3)
        val expensiveCartContent = TestCartContentFixture.cartContent(
            quantity = 3, product = TestProductFixture.product(price = 5000),
        )
        val cart = Cart(
            cartContents = listOf(cartContent, expensiveCartContent),
        )
        val coupon = BogoCoupon(expirationDate = LocalDate.of(2026, 11, 30))
        val price = cart.cartContents.sumOf { it.quantity * it.product.priceAmount() }

        // when: 할인 금액을 계산할 때
        val discountedPrice = price - coupon.discountAmount(OrderContext(items = cart.cartContents))

        // then: 할인이 적용된다
        assertThat(discountedPrice).isEqualTo(price - expensiveCartContent.product.priceAmount())
    }

    @Test
    fun `FreeShippingCoupon은 최소 주문 금액(50,000원) 미만이면 적용 불가다`() {
        // given: 주문 금액 45_000과 무료배송 쿠폰이 주어진다
        val price = 45_000
        val coupon = FreeShippingCoupon(expirationDate = LocalDate.of(2026, 11, 30))

        // when: 할인 금액을 계산할 때
        val applicable = coupon.isApplicable(OrderContext(price))

        // then: 할인을 적용할 수 없다
        assertThat(applicable).isEqualTo(false)
    }

    @Test
    fun `FreeShippingCoupon은 적용 시 배송비를 0원으로 만든다`() {
        // given: 주문 금액 50_000과 무료배송 쿠폰이 주어진다
        val price = 50_000
        val shippingPrice = 3_000
        val coupon = FreeShippingCoupon(expirationDate = LocalDate.of(2026, 11, 30))

        // when: 할인 금액을 계산할 때
        val discountedPrice = shippingPrice - coupon.discountAmount(OrderContext(price))

        // then: 할인을 적용할 수 없다
        assertThat(discountedPrice).isEqualTo(0)
    }

    @Test
    fun `MiracleMorningCoupon은 04시 이전 그리고 07시 이후면 적용할 수 없다`() {
        // given: 시간이 03시, 08시 리스트가 주어지고 미라클모닝 30% 할인 쿠폰이 주어진다
        val price = 50_000
        val coupon = MiracleMorningCoupon(expirationDate = LocalDate.of(2026, 11, 30))
        val times = listOf(LocalTime.of(3, 0), LocalTime.of(8, 0))

        // when: 할인이 가능한지 확인할 때
        val applicable = times.map { coupon.isApplicable(OrderContext(now = it)) }

        // then: 할인을 적용할 수 없다
        assertThat(applicable.all { it }).isEqualTo(false)
    }

    @Test
    fun `MiracleMorningCoupon은 04시 이후 그리고 07시 이전이면 적용할 수 있다`() {
        // given: 시간이 04시, 05:30, 07시 리스트가 주어지고 미라클모닝 30% 할인 쿠폰이 주어진다
        val price = 50_000
        val coupon = MiracleMorningCoupon(expirationDate = LocalDate.of(2026, 11, 30))
        val times = listOf(LocalTime.of(4, 0), LocalTime.of(5, 30), LocalTime.of(7, 0))

        // when: 할인이 가능한지 확인할 때
        val applicable = times.map { coupon.isApplicable(OrderContext(now = it)) }

        // then: 할인을 적용할 수 없다
        assertThat(applicable.all { it }).isEqualTo(true)
    }

    @Test
    fun `MiracleMorningCoupon은 적용 시 총 상품 금액의 30퍼센트를 할인한다`() {
        // given: 시간이 05:30으로 주어지고 미라클모닝 30% 할인 쿠폰이 주어진다
        val price = 50_000
        val coupon = MiracleMorningCoupon(expirationDate = LocalDate.of(2026, 11, 30))
        val time = LocalTime.of(5, 30)

        // when: 할인이 가능한지 확인할 때
        val discounted = price - coupon.discountAmount(OrderContext(totalPrice = price, now = time))

        // then: 할인을 적용할 수 없다
        assertThat(discounted).isEqualTo(35000)
    }

    @Test
    fun `쿠폰은 만료일을 초과하면 사용할 수 없다`() {
        // given: 다른 조건은 모두 정상값이고 만료일만 오늘 이전인 2025-05-23로 설정된 쿠폰들이 주어진다
        val expiredDate = LocalDate.of(2025, 5, 23)
        val cartContent = TestCartContentFixture.cartContent(quantity = 3)
        val context = OrderContext(
            totalPrice = 100_000,
            items = listOf(cartContent),
            now = LocalTime.of(5, 30),
        )
        val coupons = listOf(
            FixedDiscountCoupon(expirationDate = expiredDate),
            BogoCoupon(expirationDate = expiredDate),
            FreeShippingCoupon(expirationDate = expiredDate),
            MiracleMorningCoupon(expirationDate = expiredDate),
        )

        // when: 각 쿠폰의 적용 가능 여부를 확인할 때
        val applicable = coupons.map { it.isApplicable(context) }

        // then: 모든 쿠폰을 적용할 수 없다
        assertThat(applicable.all { it }).isEqualTo(false)
    }
}
