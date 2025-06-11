package woowacourse.shopping.domain.model.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product

class FixedCouponTest {
    private val coupon: FixedCoupon =
        FixedCoupon(
            id = 1,
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = "2024-11-30",
            discount = 5000,
            minimumAmount = 100000,
        )

    @Test
    fun `구매 조건을 만족하면 쿠폰이 유효하다`() {
        // given
        val validItems =
            listOf(
                CartProduct(
                    product =
                        Product(
                            id = 1,
                            imageUrl = "",
                            name = "hwannow",
                            price = 40000,
                        ),
                    quantity = 3,
                ),
            )

        // when
        val actual = coupon.isValid(validItems)

        // then
        actual shouldBe true
    }

    @Test
    fun `구매 조건을 만족하지 않으면 쿠폰이 유효하지 않다`() {
        // given
        val validItems =
            listOf(
                CartProduct(
                    product =
                        Product(
                            id = 1,
                            imageUrl = "",
                            name = "hwannow",
                            price = 4000,
                        ),
                    quantity = 3,
                ),
            )

        // when
        val actual = coupon.isValid(validItems)

        // then
        actual shouldBe false
    }

    @Test
    fun `할인 가격을 계산한다`() {
        // given
        val items =
            listOf(
                CartProduct(
                    product =
                        Product(
                            id = 1,
                            imageUrl = "",
                            name = "hwannow",
                            price = 40000,
                        ),
                    quantity = 3,
                ),
            )

        // when
        val actual = coupon.calculateDiscountAmount(items)
        val expected = coupon.discount

        // then
        actual shouldBe expected
    }
}
