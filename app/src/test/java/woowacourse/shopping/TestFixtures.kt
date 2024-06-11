package woowacourse.shopping

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.coupon.Bogo
import woowacourse.shopping.domain.coupon.Fixed
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.domain.coupon.MiracleSale
import woowacourse.shopping.domain.toRecentProduct

val cartProducts =
    List(51) { id ->
        CartProduct(
            productId = id.toLong(),
            name = "$id",
            imgUrl = "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
            price = 10000,
            quantity = 1,
        )
    }

val cartProduct: CartProduct = cartProducts.first()

val recentProducts: List<RecentProduct> = cartProducts.map { it.toRecentProduct() }

val fixedCoupon =
    Fixed(
        1,
        "FIXED5000",
        "5,000원 할인 쿠폰",
        "fixed",
        "5000-11-30",
        5000,
        100000,
    )

val bogoCoupon =
    Bogo(
        2,
        "BOGO",
        "2개 구매 시 1개 무료 쿠폰",
        "5000-05-30",
        2,
        1,
        "buyXgetY",
    )

val freeShippingCoupon =
    FreeShipping(
        3,
        "FREESHIPPING",
        "5만원 이상 구매 시 무료 배송 쿠폰",
        expirationDate = "5000-05-30",
        minimumAmount = 50000,
        discountType = "freeShipping"
    )

val miracleSale =
    MiracleSale(
        4,
        "MIRACLESALE",
        "미라클모닝 30% 할인 쿠폰",
        expirationDate = "5000-05-30",
        discount = 3000,
        availableTimeStart = "00:00:00",
        availableTimeEnd = "23:59:59",
        discountType = "percentage"
    )

val fixedAndBogo = listOf(fixedCoupon, bogoCoupon)

