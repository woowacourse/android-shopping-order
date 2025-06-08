package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.response.CartItemContent
import woowacourse.shopping.data.model.response.CouponResponseItem
import woowacourse.shopping.data.model.response.ProductContent
import woowacourse.shopping.data.model.response.ProductResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.TimeRange
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
        amount = quantity,
    )

fun ProductResponse.toProduct() =
    Product(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun CouponResponseItem.toDomain(): Coupon {
    val expDate = LocalDate.parse(expirationDate)

    return when (discountType) {
        "fixed" ->
            Coupon.FixedAmountCoupon(
                id,
                code,
                description,
                expDate,
                discount = requireNotNull(discount),
                minimumAmount = requireNotNull(minimumAmount),
            )

        "buyXgetY" ->
            Coupon.BuyXGetYCoupon(
                id,
                code,
                description,
                expDate,
                buyQuantity = requireNotNull(buyQuantity),
                getQuantity = requireNotNull(getQuantity),
            )

        "freeShipping" ->
            Coupon.FreeShippingCoupon(
                id,
                code,
                description,
                expDate,
                minimumAmount = requireNotNull(minimumAmount),
            )

        "percentage" ->
            Coupon.PercentageCoupon(
                id,
                code,
                description,
                expDate,
                discountPercent = requireNotNull(discount),
                availableTime =
                    TimeRange(
                        start = LocalTime.parse(requireNotNull(availableTime?.start)),
                        end = LocalTime.parse(requireNotNull(availableTime?.end)),
                    ),
            )

        else -> throw IllegalArgumentException("존재하지 않는 타입 : $discountType")
    }
}
