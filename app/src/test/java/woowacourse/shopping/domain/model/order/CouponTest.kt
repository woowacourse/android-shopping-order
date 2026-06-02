package woowacourse.shopping.domain.model.order

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import java.time.LocalDate
import java.time.LocalDateTime

class CouponTest {
    @Test
    fun `2022년에 2020년까지가 유효기간인 쿠폰은 적용할 수 없다`() {
        val expiredCoupon = AlwaysApplicableCoupon("FIXED", expirationDate = LocalDate.of(2020, 1, 1))
        val order =
            Order(
                dateTime = LocalDateTime.of(2022, 1, 1, 12, 30, 0),
                items = PaymentItems(emptySet()),
                deliveryFee = DeliveryFee(0),
                discountAmount = 0,
                deliveryLocation = DeliveryLocation.STANDARD,
            )
        expiredCoupon.apply(order) shouldBe order
    }

    @Test
    fun `100000원 이상 구매한 경우 적용가능한 쿠폰은 유효기간 이전에 사용하면 할인으로 5000원이 적용된다`() {
        val coupon = AlwaysApplicableCoupon("FIXED", expirationDate = LocalDate.of(2021, 1, 1), discountAmount = 5000)
        val order =
            Order(
                dateTime = LocalDateTime.of(2020, 1, 1, 12, 30, 0),
                items =
                    PaymentItems(
                        setOf(
                            CartItem(
                                id = 1L,
                                product =
                                    Product(
                                        id = 1L,
                                        name = ProductName("근사한 상품"),
                                        price = Money(10_000),
                                        imageUrl = "",
                                        category = "근사한 카테고리",
                                    ),
                                quantity = 10,
                            ),
                        ),
                    ),
                deliveryFee = DeliveryFee(0),
                discountAmount = 0,
                deliveryLocation = DeliveryLocation.STANDARD,
            )
        coupon.apply(order) shouldBe order.copy(discountAmount = 5000, appliedCouponCode = "FIXED")
    }

    @Test
    fun `이미 쿠폰이 적용된 주문에는 쿠폰을 적용할 수 없다`() {
        val coupon = AlwaysApplicableCoupon("FIXED", expirationDate = LocalDate.of(2025, 1, 1))
        val orderWithCoupon =
            Order(
                dateTime = LocalDateTime.of(2024, 1, 1, 12, 30, 0),
                items = PaymentItems(emptySet()),
                deliveryFee = DeliveryFee(0),
                discountAmount = 3000,
                deliveryLocation = DeliveryLocation.STANDARD,
                appliedCouponCode = "OTHER_COUPON",
            )
        coupon.apply(orderWithCoupon) shouldBe orderWithCoupon
    }

    @Test
    fun `코드가 같다면 같은 쿠폰이다`() {
        val coupon = AlwaysApplicableCoupon("A", expirationDate = LocalDate.of(2021, 1, 1))
        val sameCoupon = AlwaysApplicableCoupon("A", expirationDate = LocalDate.of(2020, 1, 1))
        coupon shouldBe sameCoupon
    }

    @Test
    fun `코드가 다르다면 다른 쿠폰이다`() {
        val coupon = AlwaysApplicableCoupon("A", expirationDate = LocalDate.of(2021, 1, 1))
        val differentCoupon = AlwaysApplicableCoupon("B", expirationDate = LocalDate.of(2020, 1, 1))
        coupon shouldNotBe differentCoupon
    }
}
