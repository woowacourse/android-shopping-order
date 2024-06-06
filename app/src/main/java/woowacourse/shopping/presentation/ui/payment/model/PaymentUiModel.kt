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
            return@let getPriceDiscount(it)
        } ?: DEFAULT_DISCOUNT_PRICE

    val deliveryDiscountPrice: Int get() = couponUiModels.firstOrNull { it.isChecked }?.let {
        return@let getDeliveryDiscount(it)
    } ?: DEFAULT_DISCOUNT_PRICE
    val totalPrice get() = orderPrice + totalDeliveryPrice - discountPrice
    val totalDeliveryPrice get() = deliveryPrice - deliveryDiscountPrice


    fun getPriceDiscount(couponUiModel: CouponUiModel): Int {
        return when(couponUiModel.code) {
            FIXED5000 -> {
                couponUiModel.discount
            }

            BOGO -> {
                cartProducts.filter { it.quantity >= 3 }.maxOf { it.price }.toInt()
            }

            FREESHIPPING -> {
                0
            }

            MIRACLESALE -> {
                (orderPrice * 0.3).toInt()
            }

            else -> 0
        }
    }

    fun getDeliveryDiscount(couponUiModel: CouponUiModel): Int {
        return when(couponUiModel.code) {
            FIXED5000 -> {
                0
            }

            BOGO -> {
                0
            }

            FREESHIPPING -> {
                3000
            }

            MIRACLESALE -> {
                0
            }

            else -> 0
        }
    }


    companion object {
        const val DEFAULT_DISCOUNT_PRICE = 0
        const val DEFAULT_DELIVERY_PRICE = 3000
        const val FIXED5000 = "FIXED5000"
        const val BOGO = "BOGO"
        const val FREESHIPPING = "FREESHIPPING"
        const val MIRACLESALE = "MIRACLESALE"
    }
}