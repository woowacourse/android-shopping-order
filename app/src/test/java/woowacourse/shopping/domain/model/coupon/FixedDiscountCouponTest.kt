package woowacourse.shopping.domain.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import java.time.LocalDate
import java.time.LocalDateTime

class FixedDiscountCouponTest {
    private val now = LocalDateTime.now()

    @Test
    fun `총액이 최소금액 이상이면 할인을 적용한다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts = listOf(createCartProduct(1, 3000, 2)) // 6000원
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(1000)
    }

    @Test
    fun `총액이 최소금액 미만이면 할인을 미적용한다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts = listOf(createCartProduct(1, 2000, 2)) // 4000원
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(0)
    }

    @Test
    fun `쿠폰이 만료되었으면 사용이 불가능하다`() {
        // Given
        val expiredCoupon = createCoupon(expirationDate = LocalDate.now().minusDays(1))
        val cartProducts = listOf(createCartProduct(1, 3000, 2))
        val summary = PaymentSummary(cartProducts)

        // When
        val available = expiredCoupon.isAvailable(now, summary)

        // Then
        assertThat(available).isFalse
    }

    @Test
    fun `쿠폰이 만료되었으면 할인을 미적용한다`() {
        // Given
        val expiredCoupon = createCoupon(expirationDate = LocalDate.now().minusDays(1))
        val cartProducts = listOf(createCartProduct(1, 3000, 2))
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = expiredCoupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(0)
    }

    private fun createCoupon(expirationDate: LocalDate = LocalDate.now().plusDays(1)): FixedDiscountCoupon =
        FixedDiscountCoupon(
            id = 1L,
            code = "FIXED1000",
            description = "1000원 할인",
            expirationDate = expirationDate,
            discount = 1000,
            minimumAmount = 5000,
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
