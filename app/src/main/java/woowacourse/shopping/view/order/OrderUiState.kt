package woowacourse.shopping.view.order

import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.Coupon
import java.time.LocalDate
import java.time.LocalTime

data class OrderUiState(
    val isLoading: Boolean = true,
    val cartItems: List<CartItemDomain> = emptyList(),
    val coupons: List<CouponViewItem.CouponItem> = emptyList(),
    val shippingPrice: Int = DEFAULT_SHIPPING_PRICE,
    val orderPrice: Int = cartItems.sumOf { it.totalPrice() },
    val discount: Int = DEFAULT_DISCOUNT_PRICE,
    val selectedCoupon: Coupon? = null,
) {
    fun filterAvailableCoupons(): OrderUiState {
        return copy(
            coupons = coupons.filter { it.coupon.isAvailable() },
        )
    }

    fun cancelCoupon(couponId: Int): OrderUiState {
        val targetCoupon = coupons.firstOrNull { it.coupon.id == couponId } ?: return this
        val updatedCoupon = targetCoupon.copy(isSelected = false)
        return copy(
            coupons =
                coupons.map {
                    if (it.coupon.id == updatedCoupon.coupon.id) updatedCoupon else it
                },
            selectedCoupon = null,
            discount = if (couponId == selectedCoupon?.id) 0 else discount,
        )
    }

    fun applyCoupon(couponId: Int): OrderUiState {
        val targetCoupon = coupons.firstOrNull { it.coupon.id == couponId } ?: return this
        val updatedCoupon = targetCoupon.copy(isSelected = true)
        return copy(
            coupons =
                coupons.map {
                    if (it.coupon.id == updatedCoupon.coupon.id) updatedCoupon else it.copy(isSelected = false)
                },
            discount =
                when (targetCoupon.coupon) {
                    is Coupon.Fixed -> targetCoupon.coupon.discount
                    is Coupon.BuyXGetY ->
                        cartItems.filter { it.quantity >= targetCoupon.coupon.buyQuantity }
                            .maxOf { it.product.price } * targetCoupon.coupon.getQuantity

                    is Coupon.FreeShipping -> shippingPrice
                    is Coupon.MiracleSale -> orderPrice * (targetCoupon.coupon.discount / 100)
                    is Coupon.Etc -> discountPriceForEtcCoupon(targetCoupon.coupon)
                },
            selectedCoupon = updatedCoupon.coupon,
        )
    }

    private fun discountPriceForEtcCoupon(coupon: Coupon.Etc) =
        when {
            coupon.discount != null && coupon.minimumAmount != null -> {
                coupon.discount
            }

            coupon.discount != null && coupon.startTime != null && coupon.endTime != null -> {
                orderPrice * (discount / 100)
            }

            coupon.buyQuantity != null && coupon.getQuantity != null -> {
                cartItems.filter { it.quantity >= coupon.buyQuantity }
                    .maxOf { it.product.price } * coupon.getQuantity
            }

            coupon.minimumAmount != null -> {
                shippingPrice
            }

            else -> 0
        }

    private fun Coupon.isAvailable(): Boolean {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        if (currentDate > expirationDate) return false
        when (this) {
            is Coupon.Fixed -> if (orderPrice < minimumAmount) return false
            is Coupon.BuyXGetY -> if (cartItems.none { it.quantity >= buyQuantity }) return false
            is Coupon.FreeShipping -> if (orderPrice < minimumAmount) return false
            is Coupon.MiracleSale -> if (currentTime !in startTime..endTime) return false
            is Coupon.Etc ->
                discount != null && minimumAmount != null ||
                    discount != null && startTime != null && endTime != null ||
                    buyQuantity != null && getQuantity != null ||
                    minimumAmount != null
        }
        return true
    }

    companion object {
        private const val DEFAULT_SHIPPING_PRICE = 3_000
        private const val DEFAULT_DISCOUNT_PRICE = 0
    }
}
