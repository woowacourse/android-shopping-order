package woowacourse.shopping.domain.model.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product

class BuyXGetYCouponTest {
    private val coupon: BuyXGetYCoupon =
        BuyXGetYCoupon(
            id = 1,
            code = "BOGO",
            description = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = "2024-05-30",
            buyQuantity = 2,
            getQuantity = 1,
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
                            price = 2000,
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
                            price = 2000,
                        ),
                    quantity = 1,
                ),
            )

        // when
        val actual = coupon.isValid(validItems)

        // then
        actual shouldBe false
    }

    @Test
    fun `가장 비싼 제품에 할인을 적용한다`() {
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
                CartProduct(
                    product =
                        Product(
                            id = 1,
                            imageUrl = "",
                            name = "hwannow2",
                            price = 20000,
                        ),
                    quantity = 3,
                ),
            )

        // when
        val actual = coupon.calculateDiscountAmount(validItems)

        actual shouldBe 40000
    }
}
