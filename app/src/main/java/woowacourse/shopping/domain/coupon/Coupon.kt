package woowacourse.shopping.domain.coupon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.CartProduct

@Parcelize
sealed class Coupon(
    open val id: Int,
    open val code: String,
    open val description: String,
    open val discountType: String,
    open val expirationDate: String,
    open val discount: Int? = null,
    open val minimumAmount: Int? = null,
    open val buyQuantity: Int? = null,
    open val getQuantity: Int? = null,
    open val availableTimeStart: String? = null,
    open val availableTimeEnd: String? = null,
) : Parcelable {
    abstract fun isValid(cartProducts: List<CartProduct>): Boolean

    abstract fun getPriceDiscount(cartProducts: List<CartProduct>): Int

    abstract fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int
}
