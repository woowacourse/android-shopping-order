package woowacourse.shopping.constants

import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.CouponCode
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product
import java.time.LocalDate

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
        Coupon(
            code = CouponCode.FIXED5000,
            title = "5,000원 할인 쿠폰",
            discountPrice = 5000,
            minimumPrice = 100000,
            expiryDate = LocalDate.of(2024, 11, 30),
            buyCount = 0,
            discountRatio = 0f,
            serviceCount = 0,
            availableStartTime = 0,
            availableEndTime = 24
        ),
        Coupon(
            code = CouponCode.BOGO,
            title = "2개 구매 시 1개 무료 쿠폰",
            discountPrice = 0,
            minimumPrice = 0,
            buyCount = 2,
            discountRatio = 0f,
            serviceCount = 1,
            availableStartTime = 0,
            availableEndTime = 24,
            expiryDate = LocalDate.of(2024, 5, 30),
        ),
        Coupon(
            code = CouponCode.FREESHIPPING,
            title = "5만원 이상 구매 시 무료 배송 쿠폰",
            discountPrice = 3000,
            minimumPrice = 50000,
            expiryDate = LocalDate.of(2024, 8, 31),
            buyCount = 0,
            discountRatio = 0f,
            serviceCount = 0,
            availableStartTime = 0,
            availableEndTime = 24
        ),
        Coupon(
            code = CouponCode.MIRACLESALE,
            title = "일찍일어나는 새가 피곤함 쿠폰",
            discountPrice = 3000,
            minimumPrice = 0,
            expiryDate = LocalDate.of(2024, 7, 31),
            buyCount = 0,
            discountRatio = 0.3f,
            serviceCount = 0,
            availableStartTime = 4,
            availableEndTime = 7
        )
    )
}
