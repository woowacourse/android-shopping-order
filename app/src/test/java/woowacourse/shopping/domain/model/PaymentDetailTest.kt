package woowacourse.shopping.domain.model

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageCoupon
import java.time.LocalTime

class PaymentDetailTest {
    val selectedProducts: List<CartProduct> =
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
            CartProduct(
                product =
                    Product(
                        id = 1,
                        imageUrl = "",
                        name = "hwannow2",
                        price = 1000000,
                    ),
                quantity = 3,
            ),
        )
    private val paymentDetail = PaymentDetail(selectedProducts)

    @Test
    fun `쿠폰 종류가 Fixed일 때 할인 가격을 적용한다`() {
        // given
        val coupon: FixedCoupon =
            FixedCoupon(
                id = 1,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = "2024-11-30",
                discount = 5000,
                minimumAmount = 100000,
            )

        // when
        val actual = paymentDetail.discountByCoupon(coupon)

        // then
        actual.couponDiscount shouldBe 5000
    }

    @Test
    fun `쿠폰 종류가 Percentage일 때 할인 가격을 적용한다`() {
        val coupon: PercentageCoupon =
            PercentageCoupon(
                id = 1,
                code = "MIRACLESALE",
                description = "미라클모닝 30% 할인 쿠폰",
                expirationDate = "2024-11-30",
                discount = 30,
                availableTime =
                    AvailableTime(
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                    ),
            )

        // when
        val actual = paymentDetail.discountByCoupon(coupon)

        // then
        actual.couponDiscount shouldBe 918000
    }

    @Test
    fun `쿠폰 종류가 BuyXGetY일 때 할인 가격을 적용한다`() {
        // given
        val coupon: BuyXGetYCoupon =
            BuyXGetYCoupon(
                id = 1,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = "2024-05-30",
                buyQuantity = 2,
                getQuantity = 1,
            )

        // when
        val actual = paymentDetail.discountByCoupon(coupon)

        // then
        actual.couponDiscount shouldBe 1000000
    }

    @Test
    fun `쿠폰 종류가 FreeShipping일 때 배송비가 무료이다`() {
        // given
        val coupon: FreeShippingCoupon =
            FreeShippingCoupon(
                id = 1,
                code = "FREESHIPPING",
                description = "5만원 이상 구매 시 무료 배송 쿠폰",
                expirationDate = "2024-05-30",
                minimumAmount = 50000,
            )

        // when
        val actual = paymentDetail.discountByCoupon(coupon)

        // then
        actual.deliveryFee shouldBe 0
    }
}
