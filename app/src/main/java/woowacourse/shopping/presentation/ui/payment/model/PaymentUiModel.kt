package woowacourse.shopping.presentation.ui.payment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.CartProduct

@Parcelize
data class PaymentUiModel(
    val couponUiModels: List<CouponUiModel> = emptyList(),
    val cartProducts: List<CartProduct> = emptyList()
): Parcelable {

    val isCheckedAlready: Boolean get() = couponUiModels.any { it.isChecked }

    val cartProductIds get() = cartProducts.map { it.cartId.toInt() }
    val orderPrice get() = cartProducts.sumOf { it.price * it.quantity }
    val deliveryPrice get() = DEFAULT_DELIVERY_PRICE

    val discountPrice: Int get() = couponUiModels.firstOrNull { it.isChecked }?.let {
            return@let it.coupon.getPriceDiscount(cartProducts = cartProducts)
        } ?: DEFAULT_DISCOUNT_PRICE

    val deliveryDiscountPrice: Int get() = couponUiModels.firstOrNull { it.isChecked }?.let {
        return@let it.coupon.getDeliveryDiscount(cartProducts = cartProducts)
    } ?: DEFAULT_DISCOUNT_PRICE
    
    val totalPrice get() = orderPrice + totalDeliveryPrice - discountPrice
    val totalDeliveryPrice get() = deliveryPrice - deliveryDiscountPrice


    companion object {
        const val DEFAULT_DISCOUNT_PRICE = 0
        const val DEFAULT_DELIVERY_PRICE = 3000
        const val FIXED5000 = "FIXED5000"
        const val BOGO = "BOGO"
        const val FREESHIPPING = "FREESHIPPING"
        const val MIRACLESALE = "MIRACLESALE"
    }
}