package woowacourse.shopping.domain.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import java.time.LocalDate
import java.time.LocalDateTime

class FreeShippingCouponTest {
    private val now = LocalDateTime.now()

    @Test
    fun `총액이 최소금액 이상이면 배송비가 0으로 적용된다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts = listOf(createCartProduct(1, 3000, 2)) // 총 6000원
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.deliveryFee).isEqualTo(0)
    }

    @Test
    fun `총액이 최소금액 미만이면 배송비는 유지된다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts = listOf(createCartProduct(1, 2000, 2)) // 총 4000원
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.deliveryFee).isEqualTo(3000)
    }

    @Test
    fun `쿠폰이 만료되었으면 배송비는 유지된다`() {
        // Given
        val expiredCoupon = createCoupon(expirationDate = LocalDate.now().minusDays(1))
        val cartProducts = listOf(createCartProduct(1, 3000, 2))
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = expiredCoupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.deliveryFee).isEqualTo(3000)
    }

    @Test
    fun `쿠폰이 만료되었으면 할인을 미적용한다`() {
        // Given
        val expiredCoupon = createCoupon(expirationDate = LocalDate.now().minusDays(1))
        val cartProducts = listOf(createCartProduct(1, 3000, 2))
        val summary = PaymentSummary(cartProducts)

        // When
        val available = expiredCoupon.isAvailable(now, summary)

        // Then
        assertThat(available).isFalse
    }

    private fun createCoupon(expirationDate: LocalDate = LocalDate.now().plusDays(1)): FreeShippingCoupon =
        FreeShippingCoupon(
            id = 1L,
            code = "FREESHIP",
            description = "무료배송 쿠폰",
            expirationDate = expirationDate,
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
