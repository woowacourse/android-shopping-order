package woowacourse.shopping.utils

import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.coupon.AvailableTimeDto
import woowacourse.shopping.data.remote.dto.coupon.CouponDto
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import woowacourse.shopping.domain.model.selector.ItemSelector
import woowacourse.shopping.domain.model.product.Product
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DtoMapper {
    fun CartItemResponse.toCartItems(): List<CartItem> {
        return cartItemDto.map { it.toCartItem() }
    }

    private fun CartItemDto.toCartItem(): CartItem {
        return CartItem(
            id = id.toLong(),
            product = product.toProduct(quantity),
        )
    }

    fun CartItemQuantityDto.toQuantity(): Int {
        return quantity
    }

    fun ProductResponse.toProducts(): List<Product> {
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

    fun CouponDto.toCoupon(): Coupon{
       return Coupon(
           id = id,
           expirationDate = this.formatExpirationDate(),
           couponType = CouponType.matchCoupon(code),
           description = description,
           discountType =discountType,
           discount = discount,
           minimumAmount = minimumAmount,
           availableTime = availableTimeDto?.toAvailableTime(),
           itemSelector =  ItemSelector()
       )
    }

    private fun AvailableTimeDto.toAvailableTime(): AvailableTime {
        return AvailableTime(
            end = end,
            start = start,
        )
    }

    private fun CouponDto.formatExpirationDate(): String {
        val couponExpirationDateFormatter = DateTimeFormatter.ofPattern(ShoppingUtils.EXPIRATION_FORMAT)
        val date = LocalDate.parse(this.expirationDate)
        return date.format(couponExpirationDateFormatter)
    }
}
