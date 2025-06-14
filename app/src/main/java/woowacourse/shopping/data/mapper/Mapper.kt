package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.response.cartitem.CartItemContent
import woowacourse.shopping.data.model.response.coupon.CouponResponse
import woowacourse.shopping.data.model.response.product.ProductContent
import woowacourse.shopping.data.model.response.product.ProductResponse
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.coupon.BuyXGetYFreeCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponInfo
import woowacourse.shopping.domain.coupon.CouponType
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentageCoupon
import woowacourse.shopping.domain.coupon.TimeRange
import java.time.LocalDate
import java.time.LocalTime

fun RecentProductEntity.toProduct() =
    Product(
        id = productId,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun Product.toRecentEntity() =
    RecentProductEntity(
        productId = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = price.value,
    )

fun ProductContent.toProduct() =
    Product(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun CartItemContent.toCartItem() =
    CartItem(
        cartId = id,
        product =
            Product(
                id = product.id,
                category = product.category,
                name = product.name,
                imageUrl = product.imageUrl,
                price = Price(product.price),
            ),
        quantity = quantity,
    )

fun ProductResponse.toProduct() =
    Product(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun CouponResponse.toCoupon(): Coupon {
    val timeRange =
        if (availableTime == null) {
            null
        } else {
            TimeRange(
                LocalTime.parse(availableTime.start),
                LocalTime.parse(availableTime.end),
            )
        }
    val couponInfo =
        CouponInfo(
            id = id,
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount,
            minimumAmount = minimumAmount,
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
            availableTime = timeRange,
        )
    return when (discountType) {
        "fixed" -> FixedCoupon(couponInfo, CouponType.FIXED)
        "percentage" -> PercentageCoupon(couponInfo, CouponType.PERCENTAGE)
        "buyXgetY" -> BuyXGetYFreeCoupon(couponInfo, CouponType.BUY_X_GET_Y)
        "freeShipping" -> FreeShippingCoupon(couponInfo, CouponType.FREE_SHIPPING)
        else -> throw IllegalStateException()
    }
}
