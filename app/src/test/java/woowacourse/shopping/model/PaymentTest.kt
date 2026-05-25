package woowacourse.shopping.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.model.coupon.FixedDiscountCoupon
import java.time.LocalDate

class PaymentTest {
    @Test
    fun `결제 상품들의 총 금액을 계산한다`() {
        val payment =
            Payment(
                cartItems =
                    listOf(
                        createCartItem(price = 1_000, quantity = 2),
                        createCartItem(price = 3_000, quantity = 1),
                    ),
            )

        assertThat(payment.totalPrice).isEqualTo(Money(5_000))
    }

    @Test
    fun `선택된 쿠폰 할인 금액을 반영해 최종 결제 금액을 반환한다`() {
        val coupon =
            FixedDiscountCoupon(
                id = 1,
                code = "",
                description = "",
                expirationDate = LocalDate.of(2026, 12, 31),
                discount = 1_000,
                minimumAmount = 10_000,
            )
        val payment =
            Payment(
                cartItems = listOf(createCartItem(price = 10_000, quantity = 1)),
                selectedCoupon = coupon,
                nowDate = LocalDate.of(2026, 1, 1),
            )

        assertThat(payment.finalPrice).isEqualTo(Money(12_000))
    }

    @Test
    fun `적용 가능한 쿠폰만 반환한다`() {
        val validCoupon =
            FixedDiscountCoupon(
                id = 1,
                code = "",
                description = "",
                expirationDate = LocalDate.of(2026, 12, 31),
                discount = 1_000,
                minimumAmount = 10_000,
            )
        val invalidCoupon =
            FixedDiscountCoupon(
                id = 2,
                code = "",
                description = "",
                expirationDate = LocalDate.of(2026, 12, 31),
                discount = 2_000,
                minimumAmount = 20_000,
            )
        val payment =
            Payment(
                cartItems = listOf(createCartItem(price = 10_000, quantity = 1)),
                nowDate = LocalDate.of(2026, 1, 1),
            )

        assertThat(payment.availableCoupons(listOf(validCoupon, invalidCoupon))).containsExactly(validCoupon)
    }

    private fun createCartItem(
        price: Long,
        quantity: Int,
    ): CartItem =
        CartItem(
            product =
                Product(
                    id = price.toString(),
                    name = ProductName("상품$price"),
                    price = Money(price),
                    imageUrl = "",
                    category = "",
                ),
            quantity = quantity,
        )
}
