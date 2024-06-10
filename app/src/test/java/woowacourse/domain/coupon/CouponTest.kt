package woowacourse.domain.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.createCoupon
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.coupon.CouponType

class CouponTest {

    @Test
    fun `모든 쿠폰은 유효기간이 지나면, 할인되지 않는다`() {
        // given
        val coupons =
            List(4) {
                createCoupon(
                    code = CouponType.entries[it].toString(),
                    expirationDate = "1997-07-24"
                )
            }
        // when
        val discounts = coupons.map { it.discountPrice(100000, emptyList()) }
        // then
        discounts.forEach { discount ->
            assertThat(discount).isEqualTo(0)
        }
    }

    @Test
    fun `쿠폰 타입이 FIXED5000일 때, 조건(구매금액)을 만족하면 일정금액(5000)이 할인 된다`() {
        // given
        val coupon = createCoupon(
            code = CouponType.FIXED5000.name,
            minimumAmount = 100000,
        )
        val totalPrice = 150000
        // when
        val discount = coupon.discountPrice(totalPrice, emptyList())
        // then
        assertThat(discount).isEqualTo(5000)
    }

    @Test
    fun `쿠폰 타입이 BOGO일 때, 조건(구매수량)을 만족하면 상품 하나의 가격이 할인 된다`() {
        // given
        val coupon = createCoupon(
            code = CouponType.BOGO.name,
        )
        val orderItems = listOf(
            CartProduct(2, "2", "imgUrl", 20000, 3),
        )
        // when
        val discount = coupon.discountPrice(0, orderItems)
        // then
        assertThat(discount).isEqualTo(20000)
    }

    @Test
    fun `쿠폰 타입이 BOGO일 때, 조건(구매수량)을 만족하는 상품이 여러개면, 가격이 비싼 상품이 할인 된다`() {
        // given
        val coupon = createCoupon(
            code = CouponType.BOGO.name,
        )
        val orderItems = listOf(
            CartProduct(1, "1", "imgUrl", 10000, 3),
            CartProduct(2, "2", "imgUrl", 20000, 3),
            CartProduct(3, "3", "imgUrl", 30000, 3),
        )
        // when
        val discount = coupon.discountPrice(0, orderItems)
        // then
        assertThat(discount).isEqualTo(30000)
    }

    @Test
    fun `쿠폰 타입이 FREESHIPPING일 때, 조건(구매금액)을 만족하면 배송비(3000)가 할인 된다`() {
        // given
        val coupon = createCoupon(
            code = CouponType.FREESHIPPING.name,
            minimumAmount = 50000,
        )
        val totalPrice = 150000
        // when
        val discount = coupon.discountPrice(totalPrice, emptyList())
        // then
        assertThat(discount).isEqualTo(3000)
    }
}
