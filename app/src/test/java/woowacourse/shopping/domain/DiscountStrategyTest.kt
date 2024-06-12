package woowacourse.shopping.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.BuyXGetYDiscountStrategy
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.FixedDiscountStrategy
import woowacourse.shopping.domain.model.FreeShippingDiscountStrategy
import woowacourse.shopping.domain.model.PercentageDiscountStrategy
import woowacourse.shopping.domain.model.Product
import java.time.LocalTime

class DiscountStrategyTest {
    @Test
    fun `최소 주문 금액이 3만원 이상 1,000원을 할인받는다면, 5만원을 구매했을때 할인 받을 수 있는 가격은 1,000원이다`() {
        val ordered = listOf(Cart(0L, price = 10_000, quantity = 3), Cart(1L, price = 10_000, quantity = 2))
        val discountAmount = 1_000
        val minimumAmount = 20_000

        val discountStrategy = FixedDiscountStrategy(ordered, discountAmount, minimumAmount)

        assertAll(
            "Fixed Discount",
            { assertThat(discountStrategy.isApplicable()).isTrue() },
            { assertThat(discountStrategy.calculateDiscountAmount()).isEqualTo(1_000) },
        )
    }

    @Test
    fun `10,000원 짜리 동일한 제품을 3개를 주문하면, 할인 받을 수 있는 가격은 1개 분량의 금액인 10,000원이다`() {
        val ordered = listOf(Cart(0L, price = 10_000, quantity = 3))
        val buyX = 2
        val getY = 1

        val discountStrategy = BuyXGetYDiscountStrategy(ordered, buyX, getY)

        assertAll(
            "Buy X get Y Discount",
            { assertThat(discountStrategy.isApplicable()).isTrue() },
            { assertThat(discountStrategy.calculateDiscountAmount()).isEqualTo(10_000) },
        )
    }

    @Test
    fun `3개씩 담은 제품이 여러 개인 경우, 1개당 금액이 가장 비싼 제품 가격이 할인된다`() {
        val ordered = listOf(Cart(0L, price = 10_000, quantity = 3), Cart(1L, price = 5_000, quantity = 3))
        val buyX = 2
        val getY = 1

        val discountStrategy = BuyXGetYDiscountStrategy(ordered, buyX, getY)

        assertAll(
            "Buy X get Y Discount",
            { assertThat(discountStrategy.isApplicable()).isTrue() },
            { assertThat(discountStrategy.calculateDiscountAmount()).isEqualTo(10_000) },
        )
    }

    @Test
    fun `최소 주문 금액 10,000원 이상 시 배송비 3,000원을 할인 받는면, 4만원 구매시 할인 받을 수 있는 가격은 배송비 3,000원이다`() {
        val ordered = listOf(Cart(0L, price = 10_000, quantity = 3), Cart(1L, price = 5_000, quantity = 2))
        val shippingFee = 3_000
        val minimumAmount = 10_000

        val discountStrategy = FreeShippingDiscountStrategy(ordered, shippingFee, minimumAmount)

        assertAll(
            "Free Shipping Discount",
            { assertThat(discountStrategy.isApplicable()).isTrue() },
            { assertThat(discountStrategy.calculateDiscountAmount()).isEqualTo(shippingFee) },
        )
    }

    @Test
    fun `쿠폰 사용 시간대인 7시 - 10시 사이에 주문하면 할인율이 10% 인 경우, 8시에 만 원 구매 시 천원을 할인 받을 수 있다`() {
        val ordered = listOf(Cart(0L, price = 10_000, quantity = 1))
        val discountPercentage = 10
        val availableTime = AvailableTime(LocalTime.of(7, 0), LocalTime.of(10, 0))
        val orderedTime = LocalTime.of(8, 0)

        val discountStrategy = PercentageDiscountStrategy(ordered, discountPercentage, availableTime, orderedTime)

        assertAll(
            "Percentage Discount",
            { assertThat(discountStrategy.isApplicable()).isTrue() },
            { assertThat(discountStrategy.calculateDiscountAmount()).isEqualTo(1_000) },
        )
    }
}

private fun Product(
    id: Long,
    price: Long = 10_000L,
): Product {
    return Product(id, "product $id", "", price, "")
}

private fun Cart(
    id: Long,
    price: Long,
    quantity: Int = 1,
): Cart {
    return Cart(id, Product(id, price), quantity)
}
