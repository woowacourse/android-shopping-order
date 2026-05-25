package woowacourse.shopping.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import woowacourse.shopping.domain.model.order.AlwaysApplicableCoupon
import woowacourse.shopping.domain.model.order.DeliveryFee
import woowacourse.shopping.domain.model.order.DeliveryLocation
import woowacourse.shopping.domain.model.order.NeverApplicableCoupon
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.fake.FakeOrderRepository
import java.time.LocalDate
import java.time.LocalDateTime

class ApplyCouponUseCaseTest {
    @Test
    fun `일치하는 쿠폰 코드가 없으면 주문이 변경되지 않는다`() {
        val coupon = AlwaysApplicableCoupon("COUPON", expirationDate = LocalDate.of(2026, 1, 1))
        val repository = FakeOrderRepository(initialCoupons = listOf(coupon))

        val result = applyCouponUseCase(repository, baseOrder, couponCode = "UNKNOWN")

        result shouldBe baseOrder
    }

    @Test
    fun `일치하는 쿠폰이 있으면 할인이 적용된 주문을 반환한다`() {
        val coupon = AlwaysApplicableCoupon("COUPON", expirationDate = LocalDate.of(2026, 1, 1), discountAmount = 5000)
        val repository = FakeOrderRepository(initialCoupons = listOf(coupon))

        val result = applyCouponUseCase(repository, baseOrder, couponCode = "COUPON")

        result shouldBe baseOrder.copy(discountAmount = 5000, appliedCouponCode = "COUPON")
    }

    @Test
    fun `적용 조건을 만족하지 않는 쿠폰은 할인이 적용되지 않는다`() {
        val notApplicableCoupon = NeverApplicableCoupon("COUPON", expirationDate = LocalDate.of(2026, 1, 1))
        val repository = FakeOrderRepository(initialCoupons = listOf(notApplicableCoupon))

        val result = applyCouponUseCase(repository, baseOrder, couponCode = "COUPON")

        result shouldBe baseOrder
    }

    private val baseOrder =
        Order(
            dateTime = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
            items =
                PaymentItems(
                    setOf(
                        CartItem(
                            id = 1L,
                            product =
                                Product(
                                    id = 1L,
                                    name = ProductName("상품A"),
                                    price = Money(10_000),
                                    imageUrl = "",
                                    category = "",
                                ),
                            quantity = 5,
                        ),
                    ),
                ),
            deliveryFee = DeliveryFee(3000),
            discountAmount = 0,
            deliveryLocation = DeliveryLocation.STANDARD,
        )
}
