package woowacourse.shopping.data.mapper

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.CouponState.Companion.makeCouponState
import woowacourse.shopping.domain.model.coupon.DiscountType.Companion.getDiscountType
import woowacourse.shopping.local.entity.RecentProductEntity
import woowacourse.shopping.remote.dto.AvailableTimeDto
import woowacourse.shopping.remote.dto.CartItemDto
import woowacourse.shopping.remote.dto.CartResponse
import woowacourse.shopping.remote.dto.CouponDto
import woowacourse.shopping.remote.dto.ProductDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun List<RecentProductEntity>.toRecentProducts(): List<RecentProduct> {
    return this.map { recentProductEntity -> recentProductEntity.toRecentProduct() }
}

fun RecentProductEntity.toRecentProduct(): RecentProduct {
    return RecentProduct(
        productId = this.productId,
        productName = this.productName,
        imageUrl = this.imageUrl,
        dateTime = LocalDateTime.parse(dateTime),
        category = this.category,
    )
}

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

fun String.toCartItemId(): Int {
    return split("/").last().toInt()
}
