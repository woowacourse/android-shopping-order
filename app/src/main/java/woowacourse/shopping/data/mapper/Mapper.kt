package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.db.RecentProductEntity
import woowacourse.shopping.data.model.response.cartitem.CartItemContent
import woowacourse.shopping.data.model.response.coupon.CouponResponse
import woowacourse.shopping.data.model.response.product.ProductContent
import woowacourse.shopping.data.model.response.product.ProductResponse
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product

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

fun CouponResponse.toCoupon() =
    Coupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate,
        discount = discount,
        minimumAmount = minimumAmount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        availableTimeStart = availableTime?.start,
        availableTimeEnd = availableTime?.end,
    )
