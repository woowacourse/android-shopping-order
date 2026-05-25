package woowacourse.shopping.domain

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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

class GetAvailableCouponUseCaseTest {
    @Test
    fun `적용 가능한 쿠폰 전부를 반환한다`() {
        val couponA = AlwaysApplicableCoupon("COUPON_A", expirationDate = LocalDate.of(2026, 1, 1))
        val couponB = AlwaysApplicableCoupon("COUPON_B", expirationDate = LocalDate.of(2026, 1, 1))
        val repository = FakeOrderRepository(initialCoupons = listOf(couponA, couponB))

        val result = getAvailableCouponUseCase(repository, fakeOrder)

        result shouldContainExactlyInAnyOrder listOf(couponA, couponB)
    }

    @Test
    fun `적용 불가능한 쿠폰은 제외하고 반환한다`() {
        val applicableCoupon = AlwaysApplicableCoupon("APPLICABLE", expirationDate = LocalDate.of(2026, 1, 1))
        val notApplicableCoupon = NeverApplicableCoupon("NOT_APPLICABLE", expirationDate = LocalDate.of(2026, 1, 1))
        val repository = FakeOrderRepository(initialCoupons = listOf(applicableCoupon, notApplicableCoupon))

        val result = getAvailableCouponUseCase(repository, fakeOrder)

        result shouldContainExactlyInAnyOrder listOf(applicableCoupon)
    }

    @Test
    fun `만료된 쿠폰은 반환하지 않는다`() {
        val expiredCoupon = AlwaysApplicableCoupon("EXPIRED", expirationDate = LocalDate.of(2020, 1, 1))
        val validCoupon = AlwaysApplicableCoupon("VALID", expirationDate = LocalDate.of(2026, 1, 1))
        val repository = FakeOrderRepository(initialCoupons = listOf(expiredCoupon, validCoupon))

        val result = getAvailableCouponUseCase(repository, fakeOrder)

        result shouldContainExactlyInAnyOrder listOf(validCoupon)
    }

    @Test
    fun `이미 쿠폰이 적용된 주문에는 어떤 쿠폰도 반환되지 않는다`() {
        val couponA = AlwaysApplicableCoupon("COUPON_A", expirationDate = LocalDate.of(2026, 1, 1))
        val couponB = AlwaysApplicableCoupon("COUPON_B", expirationDate = LocalDate.of(2026, 1, 1))
        val repository = FakeOrderRepository(initialCoupons = listOf(couponA, couponB))
        val orderWithCoupon = fakeOrder.copy(appliedCouponCode = "COUPON_A")

        val result = getAvailableCouponUseCase(repository, orderWithCoupon)

        result.shouldBeEmpty()
    }

    private val fakeOrder =
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
