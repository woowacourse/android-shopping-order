package woowacourse.shopping.presentation.ui.payment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.CartProduct

@Parcelize
data class PaymentUiModel(
    val couponUiModels: List<CouponUiModel> = emptyList(),
    val cartProducts: List<CartProduct> = emptyList(),
) : Parcelable {
    val cartProductIds get() = cartProducts.map { it.cartId.toInt() }
    val orderPrice get() = cartProducts.sumOf { it.price * it.quantity }

    val priceDiscount: Int get() =
        couponUiModels.firstOrNull { it.isChecked }?.let {
            return@let it.coupon.getPriceDiscount(cartProducts = cartProducts)
        } ?: DEFAULT_DISCOUNT_PRICE

    val deliveryDiscount: Int get() =
        couponUiModels.firstOrNull { it.isChecked }?.let {
            return@let it.coupon.getDeliveryDiscount(cartProducts = cartProducts)
        } ?: DEFAULT_DISCOUNT_PRICE

    val totalPrice get() = orderPrice + totalDeliveryPrice - priceDiscount
    val totalDeliveryPrice get() = DEFAULT_DELIVERY_PRICE - deliveryDiscount

    companion object {
        const val DEFAULT_DISCOUNT_PRICE = 0
        const val DEFAULT_DELIVERY_PRICE = 3000
    }
}
