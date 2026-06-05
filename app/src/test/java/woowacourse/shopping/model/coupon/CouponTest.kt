package woowacourse.shopping.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Payment
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductName
import java.time.LocalDate
import java.time.LocalTime

class CouponTest {
    @Test
    fun `고정 금액 할인 쿠폰은 최소 주문 금액 이상이면 true를 반환한다`() {
        val coupon = createFixedDiscountCoupon(minimumAmount = 10_000)
        val payment = createPayment(totalPrice = 10_000)

        assertThat(coupon.isValid(payment)).isTrue()
    }

    @Test
    fun `고정 금액 할인 쿠폰은 최소 주문 금액 미만이면 false를 반환한다`() {
        val coupon = createFixedDiscountCoupon(minimumAmount = 10_000)
        val payment = createPayment(totalPrice = 9_999)

        assertThat(coupon.isValid(payment)).isFalse()
        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(0))
    }

    @Test
    fun `고정 금액 할인 쿠폰은 정해진 금액만큼 할인해준다`() {
        val coupon = createFixedDiscountCoupon(discount = 1_000, minimumAmount = 10_000)
        val payment = createPayment(totalPrice = 10_000)

        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(1_000))
    }

    @Test
    fun `퍼센트 할인 쿠폰은 사용 가능 시간 내라면 true를 반환한다`() {
        val coupon =
            createPercentageDiscountCoupon(
                availableTime =
                    AvailableTime(
                        start = LocalTime.of(11, 0),
                        end = LocalTime.of(14, 0),
                    ),
            )
        val payment =
            createPayment(
                totalPrice = 10_000,
                nowTime = LocalTime.of(12, 0),
            )

        assertThat(coupon.isValid(payment)).isTrue()
    }

    @Test
    fun `퍼센트 할인 쿠폰은 사용 가능 시간 밖이면 false를 반환한다`() {
        val coupon =
            createPercentageDiscountCoupon(
                availableTime =
                    AvailableTime(
                        start = LocalTime.of(11, 0),
                        end = LocalTime.of(14, 0),
                    ),
            )
        val payment =
            createPayment(
                totalPrice = 10_000,
                nowTime = LocalTime.of(15, 0),
            )

        assertThat(coupon.isValid(payment)).isFalse()
        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(0))
    }

    @Test
    fun `퍼센트 할인 쿠폰은 주문 금액의 비율만큼 할인해준다`() {
        val coupon = createPercentageDiscountCoupon(discountRate = 10)
        val payment = createPayment(totalPrice = 10_000)

        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(1_000))
    }

    @Test
    fun `무료 배송 쿠폰은 최소 주문 금액 이상이면 배송비만큼 할인해준다`() {
        val coupon = createFreeShippingCoupon(minimumAmount = 10_000)
        val payment = createPayment(totalPrice = 10_000)

        assertThat(coupon.isValid(payment)).isTrue()
        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(3_000))
    }

    @Test
    fun `X 더하기 Y 쿠폰은 한 묶음 이상 구매하면 true를 반환한다`() {
        val coupon = createBuyXGetYCoupon(buyQuantity = 1, getQuantity = 1)
        val payment = createPayment(totalPrice = 5_000, quantity = 2)

        assertThat(coupon.isValid(payment)).isTrue()
    }

    @Test
    fun `X 더하기 Y 쿠폰은 한 묶음 미만 구매하면 false를 반환한다`() {
        val coupon = createBuyXGetYCoupon(buyQuantity = 1, getQuantity = 1)
        val payment = createPayment(totalPrice = 5_000, quantity = 1)

        assertThat(coupon.isValid(payment)).isFalse()
        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(0))
    }

    @Test
    fun `X 더하기 Y 쿠폰은 무료 수량만큼 상품 가격을 할인해준다`() {
        val coupon = createBuyXGetYCoupon(buyQuantity = 1, getQuantity = 1)
        val payment = createPayment(totalPrice = 5_000, quantity = 4)

        assertThat(coupon.calculateDiscount(payment)).isEqualTo(Money(10_000))
    }

    private fun createPayment(
        totalPrice: Long,
        quantity: Int = 1,
        nowDate: LocalDate = LocalDate.of(2026, 1, 1),
        nowTime: LocalTime = LocalTime.NOON,
    ): Payment =
        Payment(
            cartItems = listOf(createCartItem(price = totalPrice, quantity = quantity)),
            nowDate = nowDate,
            nowTime = nowTime,
        )

    private fun createCartItem(
        price: Long,
        quantity: Int,
    ): CartItem =
        CartItem(
            product =
                Product(
                    id = "1",
                    name = ProductName("상품"),
                    price = Money(price),
                    imageUrl = "",
                    category = "category",
                ),
            quantity = quantity,
        )

    private fun createFixedDiscountCoupon(
        discount: Int = 1_000,
        minimumAmount: Long = 10_000,
    ): FixedDiscountCoupon =
        FixedDiscountCoupon(
            id = 1,
            code = "",
            description = "",
            expirationDate = LocalDate.of(2026, 12, 31),
            discount = discount,
            minimumAmount = minimumAmount,
        )

    private fun createPercentageDiscountCoupon(
        discountRate: Int = 10,
        availableTime: AvailableTime =
            AvailableTime(
                start = LocalTime.MIN,
                end = LocalTime.MAX,
            ),
    ): PercentageDiscountCoupon =
        PercentageDiscountCoupon(
            id = 2,
            code = "",
            description = "",
            expirationDate = LocalDate.of(2026, 12, 31),
            discountRate = discountRate,
            availableTime = availableTime,
        )

    private fun createFreeShippingCoupon(minimumAmount: Long = 10_000): FreeShippingCoupon =
        FreeShippingCoupon(
            id = 3,
            code = "",
            description = "",
            expirationDate = LocalDate.of(2026, 12, 31),
            minimumAmount = minimumAmount,
        )

    private fun createBuyXGetYCoupon(
        buyQuantity: Int,
        getQuantity: Int,
    ): BuyXGetYCoupon =
        BuyXGetYCoupon(
            id = 4,
            code = "",
            description = "",
            expirationDate = LocalDate.of(2026, 12, 31),
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
        )
}
