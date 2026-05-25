package woowacourse.shopping.model

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.Product
import java.time.LocalDate
import java.time.LocalTime

class CouponTest {
    @Test
    fun `10만원 이상이면 5천원 할인 쿠폰을 적용한다`() {
        val coupon =
            Coupon.FixedAmount(
                id = 1,
                title = "5,000원 할인 쿠폰",
                code = "FIXED5000",
                expiryDate = LocalDate.of(2024, 11, 30),
                amount = 5_000,
                minOrderAmount = 100_000,
            )
        val items = listOf(cartItem(price = 100_000, quantity = 1))

        coupon.discount(items = items, shippingFee = 3_000) shouldBe 5_000
    }

    @Test
    fun `BOGO 쿠폰은 3개 이상 담긴 상품 중 가장 비싼 1개 금액을 할인한다`() {
        val coupon =
            Coupon.BuyOneGetOne(
                id = 2,
                title = "2개 구매 시 1개 무료 쿠폰",
                code = "BOGO",
                expiryDate = LocalDate.of(2024, 5, 30),
                buyQuantity = 2,
                freeQuantity = 1,
            )
        val items =
            listOf(
                cartItem(price = 10_000, quantity = 3),
                cartItem(price = 20_000, quantity = 3),
            )

        coupon.discount(items = items, shippingFee = 3_000) shouldBe 20_000
    }

    @Test
    fun `5만원 이상이면 무료 배송 쿠폰을 적용한다`() {
        val coupon =
            Coupon.FreeShipping(
                id = 3,
                title = "5만원 이상 구매 시 무료 배송 쿠폰",
                code = "FREESHIPPING",
                expiryDate = LocalDate.of(2024, 8, 31),
                minOrderAmount = 50_000,
            )
        val items = listOf(cartItem(price = 50_000, quantity = 1))

        coupon.discount(items = items, shippingFee = 3_000) shouldBe 3_000
    }

    @Test
    fun `미라클모닝 쿠폰은 오전 4시부터 7시 전까지 30퍼센트를 할인한다`() {
        val coupon =
            Coupon.TimeRate(
                id = 4,
                title = "미라클모닝 30% 할인 쿠폰",
                code = "MIRACLESALE",
                expiryDate = LocalDate.of(2024, 7, 31),
                rate = 0.3,
                startTime = LocalTime.of(4, 0),
                endTime = LocalTime.of(7, 0),
            )
        val items = listOf(cartItem(price = 10_000, quantity = 2))

        coupon.discount(items = items, shippingFee = 3_000, now = LocalTime.of(4, 0)) shouldBe 6_000
        coupon.discount(items = items, shippingFee = 3_000, now = LocalTime.of(7, 0)) shouldBe 0
    }

    private fun cartItem(
        price: Long,
        quantity: Int,
    ): CartItem =
        CartItem(
            product =
                Product(
                    name = "상품",
                    price = Money(price),
                    imageUrl = "",
                ),
            quantity = quantity,
        )
}
