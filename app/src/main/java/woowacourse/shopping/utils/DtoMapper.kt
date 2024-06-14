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
import woowacourse.shopping.domain.model.coupon.BogoDiscountStrategy
import woowacourse.shopping.domain.model.coupon.FixedDiscountStrategy
import woowacourse.shopping.domain.model.coupon.FreeShippingStrategy
import woowacourse.shopping.domain.model.coupon.TimeBasedDiscountStrategy
import java.time.LocalDate
import java.time.LocalTime

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
        return Coupon(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate.toLocalDate(),
            discountType = discountType,
            minimumAmount = minimumAmount ?: 0,
            discount = discount,
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
            availableTime =
                availableTime?.let {
                    Coupon.AvailableTime(
                        start = it.start.toLocalTime(),
                        end = it.end.toLocalTime(),
                    )
                },
            discountStrategy = toDiscountStrategy(),
        )
    }

    private fun String.toLocalDate(): LocalDate {
        return LocalDate.parse(this)
    }

    private fun String.toLocalTime(): LocalTime {
        return LocalTime.parse(this)
    }

    private fun CouponDto.toDiscountStrategy() =
        when {
            discount != null && availableTime == null -> FixedDiscountStrategy(discount)
            buyQuantity != null && getQuantity != null -> BogoDiscountStrategy(buyQuantity, getQuantity)
            discount == null && minimumAmount != null -> FreeShippingStrategy(3000)
            discount != null && availableTime != null -> {
                val startTime = availableTime.start.toLocalTime()
                val endTime = availableTime.end.toLocalTime()
                TimeBasedDiscountStrategy(discount, startTime, endTime)
            }
            else -> throw IllegalArgumentException("알 수 없는 쿠폰 정보")
        }
}
