package woowacourse.shopping.model

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponDetail
import woowacourse.shopping.domain.model.CouponDiscountType.BUY_X_GET_Y
import woowacourse.shopping.domain.model.CouponDiscountType.FIXED
import woowacourse.shopping.domain.model.CouponDiscountType.FREE_SHIPPING
import woowacourse.shopping.domain.model.CouponDiscountType.PERCENTAGE
import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.model.FixedDiscountCoupon
import woowacourse.shopping.domain.model.FreeShippingCoupon
import woowacourse.shopping.domain.model.HistoryProduct
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.PercentageDiscountCoupon
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.QuantityBonusCoupon
import java.time.LocalDate
import java.time.LocalTime

val DUMMY_PRODUCT_DETAIL_1 =
    ProductDetail(
        id = 1,
        name = "[병천아우내] 모듬순대",
        imageUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/00fb05f8-cb19-4d21-84b1-5cf6b9988749.jpg",
        price = 11900,
        category = "공백제이",
    )

val DUMMY_PRODUCT_DETAIL_2 =
    ProductDetail(
        id = 2,
        name = "[빙그래] 요맘때 파인트 710mL 3종 (택1)",
        imageUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/73061aab-a2e2-443a-b0f9-f19b7110045e.jpg",
        price = 5000,
        category = "공백제이",
    )

val DUMMY_PRODUCT_DETAIL_3 =
    ProductDetail(
        id = 3,
        name = "[애슐리] 크런치즈엣지 포테이토피자 495g",
        imageUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/23efcafe-0765-478f-afe9-f9af7bb9b7df.jpg",
        price = 10900,
        category = "공백제이",
    )

val DUMMY_PRODUCT_DETAIL_4 =
    ProductDetail(
        id = 4,
        name = "치밥하기 좋은 순살 바베큐치킨",
        imageUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/f864b361-85da-4482-aec8-909397caac4e.jpg",
        price = 13990,
        category = "공백제이",
    )

val DUMMY_PRODUCT_1 = Product(DUMMY_PRODUCT_DETAIL_1, 1, 5)
val DUMMY_PRODUCT_2 = Product(DUMMY_PRODUCT_DETAIL_2, 2, 6)
val DUMMY_PRODUCT_3 = Product(DUMMY_PRODUCT_DETAIL_3, 3, 7)
val DUMMY_PRODUCT_4 = Product(DUMMY_PRODUCT_DETAIL_4, 4)

val DUMMY_HISTORY_PRODUCT_1 =
    HistoryProduct(
        productId = DUMMY_PRODUCT_DETAIL_1.id,
        name = DUMMY_PRODUCT_DETAIL_1.name,
        imageUrl = DUMMY_PRODUCT_DETAIL_1.imageUrl,
        category = "공백제이",
    )

val DUMMY_PRODUCTS_1 =
    Products(
        products =
            listOf(
                DUMMY_PRODUCT_1,
                DUMMY_PRODUCT_2,
                DUMMY_PRODUCT_3,
            ),
        page = Page(2, isFirst = false, isLast = false),
    )

val DUMMY_PRODUCTS_2 =
    Products(
        products = listOf(DUMMY_PRODUCT_1),
    )

val DUMMY_PRODUCTS_3 =
    Products(
        products = listOf(DUMMY_PRODUCT_4),
    )

val DUMMY_COUPON_1: Coupon =
    FixedDiscountCoupon(
        detail =
            CouponDetail(
                id = 1,
                code = "FIXED5000",
                name = "5,000원 할인 쿠폰",
                expirationDate = LocalDate.of(2025, 11, 30),
                discount = 5000,
                minimumPurchase = 100000,
                discountType = FIXED,
                buyQuantity = null,
                getQuantity = null,
                availableTime = null,
            ),
        isSelected = false,
    )

val DUMMY_COUPON_2: Coupon =
    QuantityBonusCoupon(
        detail =
            CouponDetail(
                id = 2,
                code = "BOGO",
                name = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = LocalDate.of(2025, 6, 30),
                discount = null,
                minimumPurchase = null,
                discountType = BUY_X_GET_Y,
                buyQuantity = 2,
                getQuantity = 1,
                availableTime = null,
            ),
        isSelected = false,
    )

val DUMMY_COUPON_3: Coupon =
    FreeShippingCoupon(
        detail =
            CouponDetail(
                id = 3,
                code = "FREESHIPPING",
                name = "5만원 이상 구매 시 무료 배송 쿠폰",
                expirationDate = LocalDate.of(2025, 8, 31),
                discount = null,
                minimumPurchase = 50000,
                discountType = FREE_SHIPPING,
                buyQuantity = null,
                getQuantity = null,
                availableTime = null,
            ),
        isSelected = false,
    )

val DUMMY_COUPON_4: Coupon =
    PercentageDiscountCoupon(
        detail =
            CouponDetail(
                id = 4,
                code = "MIRACLESALE",
                name = "미라클모닝 30% 할인 쿠폰",
                expirationDate = LocalDate.of(2025, 7, 31),
                discount = 30,
                minimumPurchase = null,
                discountType = PERCENTAGE,
                buyQuantity = null,
                getQuantity = null,
                availableTime =
                    CouponDetail.AvailableTime(
                        start = LocalTime.of(4, 0, 0),
                        end = LocalTime.of(7, 0, 0),
                    ),
            ),
        isSelected = false,
    )

val DUMMY_COUPONS_1: Coupons = Coupons(listOf(DUMMY_COUPON_1, DUMMY_COUPON_2, DUMMY_COUPON_3, DUMMY_COUPON_4))
