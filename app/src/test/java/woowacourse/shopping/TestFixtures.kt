package woowacourse.shopping

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.coupon.AvailableTime
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponType

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

val coupons = List(4) { createCoupon(id = it) }

fun createCoupon(
    id: Int = 1,
    code: String = CouponType.FIXED5000.name,
    expirationDate: String = "2025-12-31",
    discountType: String = "FIXED",
    description: String = "description",
    discount: Int = 5000,
    minimumAmount: Int = 100000,
    buyQuantity: Int = 0,
    getQuantity: Int = 0,
    availableTime: AvailableTime? = null,
): Coupon {
    return Coupon(
        id = id,
        code = code,
        expirationDate = expirationDate,
        discountType = discountType,
        description = description,
        discount = discount,
        minimumAmount = minimumAmount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        availableTime = availableTime,
    )
}
