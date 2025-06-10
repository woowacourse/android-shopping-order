package woowacourse.shopping.domain.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PercentageDiscountCouponTest {
    private val now = LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)) // 오후 2시

    @Test
    fun `할인 가능한 시간대에 쿠폰이 적용되면 할인 금액이 적용된다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts = listOf(createCartProduct(1, 10000, 1)) // 총액: 10000
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(1000) // 10% 할인
    }

    @Test
    fun `할인 불가능한 시간대에는 할인 금액이 적용되지 않는다`() {
        // Given
        val earlyMorning = LocalDateTime.of(LocalDate.now(), LocalTime.of(3, 0))
        val coupon = createCoupon()
        val cartProducts = listOf(createCartProduct(1, 10000, 1))
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, earlyMorning)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(0)
    }

    @Test
    fun `쿠폰이 만료되었으면 할인 금액이 적용되지 않는다`() {
        // Given
        val expiredCoupon = createCoupon(expirationDate = LocalDate.now().minusDays(1))
        val cartProducts = listOf(createCartProduct(1, 10000, 1))
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = expiredCoupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(0)
    }

    private fun createCoupon(
        expirationDate: LocalDate = LocalDate.now().plusDays(1),
        discount: Int = 10,
        availableTime: AvailableTime = AvailableTime(LocalTime.of(9, 0), LocalTime.of(18, 0)),
    ) = PercentageDiscountCoupon(
        id = 1L,
        code = "PERCENT10",
        description = "10% 할인",
        expirationDate = expirationDate,
        discount = discount,
        availableTime = availableTime,
    )

    private fun createCartProduct(
        id: Long,
        price: Int,
        quantity: Int,
    ): CartProduct {
        val product = Product(id, "상품$id", "", Price(price), "")
        return CartProduct(
            cartId = id,
            product = product,
            quantity = quantity,
        )
    }
}
