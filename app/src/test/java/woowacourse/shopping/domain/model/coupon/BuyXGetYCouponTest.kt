package woowacourse.shopping.domain.model.coupon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import java.time.LocalDate
import java.time.LocalDateTime

class BuyXGetYCouponTest {
    private val now = LocalDateTime.now()

    @Test
    fun `수량 조건을 만족하는 경우 쿠폰이 적용 가능하다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts =
            listOf(
                createCartProduct(1, 1000, 3), // 3개 ≥ 2+1
            )
        val summary = PaymentSummary(cartProducts)

        // When
        val result = coupon.isAvailable(now, summary)

        // Then
        assertThat(result).isTrue
    }

    @Test
    fun `수량 조건을 만족하지 않으면 쿠폰이 적용되지 않는다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts =
            listOf(
                createCartProduct(1, 1000, 2), // 2개 < 2+1
            )
        val summary = PaymentSummary(cartProducts)

        // When
        val result = coupon.isAvailable(now, summary)

        // Then
        assertThat(result).isFalse
    }

    @Test
    fun `조건을 만족하면 가장 비싼 상품 가격만큼 할인이 적용된다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts =
            listOf(
                createCartProduct(1, 1000, 3),
                createCartProduct(2, 3000, 3), // 가장 비쌈
                createCartProduct(3, 2000, 1), // 조건 미달
            )
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(3000)
    }

    @Test
    fun `조건을 만족하지 않으면 할인은 0이다`() {
        // Given
        val coupon = createCoupon()
        val cartProducts =
            listOf(
                createCartProduct(1, 1000, 1),
                createCartProduct(2, 2000, 2),
            )
        val summary = PaymentSummary(cartProducts)

        // When
        val discounted = coupon.calculateDiscountAmount(summary, now)

        // Then
        assertThat(discounted.discountPrice).isEqualTo(0)
    }

    private fun createCoupon(): BuyXGetYCoupon =
        BuyXGetYCoupon(
            id = 1L,
            code = "BUY2GET1",
            description = "2개 사면 1개 무료",
            expirationDate = LocalDate.now().plusDays(1),
            buyQuantity = 2,
            getQuantity = 1,
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
