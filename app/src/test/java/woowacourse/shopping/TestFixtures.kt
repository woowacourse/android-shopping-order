package woowacourse.shopping

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.coupon.Bogo
import woowacourse.shopping.domain.coupon.Fixed
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
        "2300-11-30",
        5000,
        100000,
    )

val Bogo =
    Bogo(
        2,
        "BOGO",
        "2개 구매 시 1개 무료 쿠폰",
        "2300-05-30",
        2,
        1,
        "buyXgetY",
    )

val fixedAndBogo = listOf(fixedCoupon, Bogo)
