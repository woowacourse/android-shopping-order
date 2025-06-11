package woowacourse.shopping.domain.model.coupon

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product

class FreeShippingCouponTest {
    private val coupon: FreeShippingCoupon =
        FreeShippingCoupon(
            id = 1,
            code = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송 쿠폰",
            expirationDate = "2024-05-30",
            minimumAmount = 50000,
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
                            price = 20000,
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
                    quantity = 3,
                ),
            )

        // when
        val actual = coupon.isValid(validItems)

        // then
        actual shouldBe false
    }
}
