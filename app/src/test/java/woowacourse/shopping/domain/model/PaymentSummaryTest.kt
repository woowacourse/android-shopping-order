package woowacourse.shopping.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.fixture.productsFixture

class PaymentSummaryTest {
    private val cartProducts = productsFixture.map { CartProduct(it.id, it.toDomain(), 1) }

    @Test
    fun `주문 금액을 계산할 수 있다`() {
        // When
        val summary = PaymentSummary(products = cartProducts)

        // Then
        val expected = cartProducts.sumOf { it.totalPrice }
        assertThat(summary.orderPrice).isEqualTo(expected)
    }

    @Test
    fun `최종 결제 금액을 계산할 수 있다`() {
        // Given
        val discount = 500
        val delivery = 3000
        val summary =
            PaymentSummary(
                products = cartProducts,
                discountPrice = discount,
                deliveryFee = delivery,
            )

        // When
        val expectedFinalPrice = summary.orderPrice - discount + delivery

        // Then
        assertThat(summary.finalPaymentPrice).isEqualTo(expectedFinalPrice)
    }
}
