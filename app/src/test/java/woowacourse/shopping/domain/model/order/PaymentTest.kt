package woowacourse.shopping.domain.model.order

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.coupon.FixedAmountCoupon
import woowacourse.shopping.domain.model.product.Product
import java.time.LocalDate

class PaymentTest {
    @Test
    fun `결제 금액은 주문 금액에서 쿠폰 할인을 빼고 배송비를 더한다`() {
        val payment =
            Payment(
                order = Order(
                    PurchaseProducts(
                        listOf(
                            purchaseProduct(
                                price = 204_200,
                                count = 1
                            )
                        )
                    )
                ),
                selectedCoupon =
                    FixedAmountCoupon(
                        code = "FIXED5000",
                        name = "5,000원 할인 쿠폰",
                        expirationDate = LocalDate.of(2024, 11, 30),
                        discountAmount = 5_000,
                        minimumOrderAmount = 100_000,
                    ),
            )

        payment.orderAmount shouldBe 204_200
        payment.couponDiscountAmount shouldBe 5_000
        payment.deliveryFee shouldBe 3_000
        payment.totalPaymentAmount shouldBe 202_200
    }

    @Test
    fun `쿠폰 할인 금액은 주문 금액을 초과할 수 없다`() {
        val payment =
            Payment(
                order =
                    Order(
                        PurchaseProducts(
                            listOf(
                                purchaseProduct(
                                    price = 3_000,
                                    count = 1,
                                ),
                            ),
                        ),
                    ),
                selectedCoupon =
                    FixedAmountCoupon(
                        code = "FIXED5000",
                        name = "5,000원 할인 쿠폰",
                        expirationDate = LocalDate.of(2024, 11, 30),
                        discountAmount = 5_000,
                        minimumOrderAmount = 0,
                    ),
            )

        payment.couponDiscountAmount shouldBe 3_000
        payment.totalPaymentAmount shouldBe 3_000
    }

    private fun purchaseProduct(
        price: Int,
        count: Int,
    ) =
        PurchaseProduct(
            id = 1L,
            product =
                Product(
                    category = "category",
                    id = 1L,
                    imageUri = "uri",
                    name = "상품",
                    price = price,
                ),
            count = count,
        )
}
