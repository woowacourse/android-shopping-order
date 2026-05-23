package woowacourse.shopping.constants

import java.time.LocalDate
import java.time.LocalTime
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentageCoupon

object MockData {
    private const val IMAGE_BASE_URL =
        "https://github.com/CommitTheKermit/android-shopping-cart/blob/step1/images/product_image"
    private const val IMAGE_URL_SUFFIX = ".png?raw=true"

    val MOCK_PRODUCTS: List<Product> = (1..35).map { i ->
        Product(
            id = i.toString(),
            name = "품목$i",
            price = Money(i * 1_000),
            imageUrl = "$IMAGE_BASE_URL${(i - 1) % 5}$IMAGE_URL_SUFFIX",
        )
    }

    val MOCK_COUPONS: List<Coupon> = listOf(
        FixedDiscountCoupon(
            id = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = LocalDate.of(2026, 11, 30),
            minimumPrice = 100_000,
            discountPrice = 5_000,
        ),
        BuyXGetYCoupon(
            id = "BOGO",
            description = "2개 구매 시 1개 무료 쿠폰",
            buyQuantity = 2,
            getQuantity = 1,
            expirationDate = LocalDate.of(2026, 5, 30),
        ),
        FreeShippingCoupon(
            id = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송 쿠폰",
            expirationDate = LocalDate.of(2026, 8, 31),
            minimumPrice = 50_000,
        ),
        PercentageCoupon(
            id = "MIRACLESALE",
            description = "미라클모닝 30% 할인 쿠폰",
            expirationDate = LocalDate.of(2026, 7, 31),
            startTime = LocalTime.of(4, 0),
            endTime = LocalTime.of(7, 0),
        ),
    )
}
