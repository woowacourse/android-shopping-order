import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.ui.model.OrderInformation
import java.time.LocalDate

object TestFixture {
    val orderInformation =
        OrderInformation(
            listOf(
                CartItem(
                    id = 16911,
                    quantity = 3,
                    product =
                        Product(
                            45,
                            """https://images.unsplash.com/photo-1647503380147-
                                |e075b24f4cbe?q=80&w=3540&auto=format&fit=crop&ixlib
                                |=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D
                            """.trimMargin(),
                            "전자책리더기",
                            200000,
                            3,
                        ),
                ),
                CartItem(
                    id = 16913,
                    quantity = 2,
                    product =
                        Product(
                            47,
                            """"https://images.unsplash.com/photo-1638740396066-
                                |e5186f6dd452?q=80&w=3538&auto=format&fit=crop&ixlib=
                                |rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D""".trimMargin(),
                            "무선마우스",
                            50000,
                            2,
                        ),
                ),
            ),
        )

    val fixedDiscountCoupon =
        FixedDiscountCoupon(
            id = 1,
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = LocalDate.of(2024, 11, 30),
            discount = 5_000,
            minimumAmount = 100_000,
            discountType = "fixed",
        )

    val buyXGetYCoupon =
        BuyXGetYCoupon(
            id = 2,
            code = "BOGO",
            description = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = LocalDate.of(2024, 5, 30),
            buyQuantity = 2,
            getQuantity = 1,
            discountType = "buyXgetY",
        )

    val freeShippingCoupon =
        FreeShippingCoupon(
            id = 3,
            code = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송 쿠폰",
            expirationDate = LocalDate.of(2024, 8, 31),
            minimumAmount = 50_000,
            discountType = "freeShipping",
        )
}
