package woowacourse.shopping.constants

import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalTime

object MockData {
    private const val IMAGE_BASE_URL =
        "https://github.com/CommitTheKermit/android-shopping-cart/blob/step1/images/product_image"
    private const val IMAGE_URL_SUFFIX = ".png?raw=true"

    val MOCK_PRODUCTS: List<Product> =
        (1..35).map { i ->
            Product(
                id = i.toLong(),
                name = "품목$i",
                price = Money(i * 1_000),
                imageUrl = "$IMAGE_BASE_URL${(i - 1) % 5}$IMAGE_URL_SUFFIX",
            )
        }

    val MOCK_COUPONS: List<Coupon> = listOf(
        FixedDiscountCoupon(
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            minimumAmount = 100000,
            expirationDate = LocalDate.of(2024, 11, 30),
            discount = 5000
        ),
        BuyXGetYCoupon(
            code = "BOGO",
            description = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = LocalDate.of(2024, 5, 30),
            buyQuantity = 2,
            getQuantity = 1
        ),
        FreeShippingCoupon(
            code = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송 쿠폰",
            expirationDate = LocalDate.of(2024, 12, 31),
            minimumAmount = 50000
        ),
        PercentageDiscountCoupon(
            code = "MIRACLESALE",
            description = "일찍일어나는 새가 피곤함 쿠폰",
            expirationDate = LocalDate.of(2024, 7, 31),
            discount = 0.30f,
            startTime = LocalTime.of(4, 0, 0),
            endTime = LocalTime.of(7, 0, 0)
        )
    )
}
