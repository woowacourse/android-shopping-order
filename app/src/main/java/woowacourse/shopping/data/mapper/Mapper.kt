package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.AvailableTimeDto
import woowacourse.shopping.data.dto.CartItemDto
import woowacourse.shopping.data.dto.CartResponse
import woowacourse.shopping.data.dto.CouponDto
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.local.recent.RecentProductEntity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.CouponState.Companion.makeCouponState
import woowacourse.shopping.domain.model.coupon.DiscountType.Companion.getDiscountType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun Product.toRecentProductEntity(): RecentProductEntity {
    return RecentProductEntity(
        productId = productId,
        productName = name,
        imageUrl = imageUrl,
        dateTime = LocalDateTime.now().toString(),
        category = category,
    )
}

fun ProductDto.toProduct(): Product {
    return Product(
        category,
        productId,
        imageUrl,
        name,
        price,
    )
}

fun CartResponse.toCartItems(): List<CartItem> {
    return cartItems.map { cartItem -> cartItem.toCartItem() }
}

fun CartItemDto.toCartItem(): CartItem {
    return CartItem(cartItemId, quantity, productDto.toProduct())
}

fun List<CouponDto>.toCouponStates(): List<CouponState> {
    return map { couponDto ->
        makeCouponState(couponDto.toCouPon())
    }
}

fun CouponDto.toCouPon(): Coupon {
    return Coupon(
        id,
        code,
        description,
        LocalDate.parse(expirationDate),
        discount,
        minimumAmount,
        buyQuantity,
        getQuantity,
        availableTime?.toAvailableTime(),
        getDiscountType(discountType),
    )
}

fun AvailableTimeDto.toAvailableTime(): AvailableTime {
    return AvailableTime(LocalTime.parse(start), LocalTime.parse(end))
}
