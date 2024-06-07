package woowacourse.shopping.utils

import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.order.CouponDto
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.ItemSelector
import woowacourse.shopping.domain.model.Product

object DtoMapper {
    fun CartItemResponse.toCartItemList(): List<CartItem> {
        return cartItemDto.map { it.toCartItem() }
    }

    fun CartItemDto.toCartItem(): CartItem {
        return CartItem(
            id = id,
            product = product.toProduct(quantity),
        )
    }

    fun CartItemQuantityDto.toQuantity(): Int {
        return quantity
    }

    fun ProductResponse.toProductList(): List<Product> {
        return productDto.map { it.toProduct() }
    }

    fun ProductDto.toProduct(quantity: Int = 0): Product {
        return Product(
            id = id.toLong(),
            name = name,
            imageUrl = imageUrl,
            price = price,
            category = category,
            cartItemCounter = CartItemCounter(quantity),
            itemSelector = ItemSelector(),
        )
    }

    fun CouponDto.toDomainModel(): Coupon {
        return when (this) {
            is CouponDto.FixedDiscountCouponDto ->
                Coupon.FixedDiscountCoupon(
                    id = this.id,
                    code = this.code,
                    description = this.description,
                    expirationDate = this.expirationDate,
                    discountType = this.discountType,
                    discount = this.discount,
                    minimumAmount = this.minimumAmount,
                )
            is CouponDto.BogoCouponDto ->
                Coupon.BogoCoupon(
                    id = this.id,
                    code = this.code,
                    description = this.description,
                    expirationDate = this.expirationDate,
                    discountType = this.discountType,
                    buyQuantity = this.buyQuantity,
                    getQuantity = this.getQuantity,
                )
            is CouponDto.FreeShippingCouponDto ->
                Coupon.FreeShippingCoupon(
                    id = this.id,
                    code = this.code,
                    description = this.description,
                    expirationDate = this.expirationDate,
                    discountType = this.discountType,
                    minimumAmount = this.minimumAmount,
                )
            is CouponDto.TimeBasedDiscountCouponDto ->
                Coupon.TimeBasedDiscountCoupon(
                    id = this.id,
                    code = this.code,
                    description = this.description,
                    expirationDate = this.expirationDate,
                    discountType = this.discountType,
                    discount = this.discount,
                    availableTimeStart = this.availableTime.start,
                    availableTimeEnd = this.availableTime.end,
                )
        }
    }
}
