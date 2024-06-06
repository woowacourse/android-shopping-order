package woowacourse.shopping.presentation.ui.payment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.Coupon

@Parcelize
data class CouponUiModel (
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTimeStart: String? = null,
    val availableTimeEnd: String? = null,
    val discountType: String,
    val isChecked: Boolean = false
): Parcelable

fun Coupon.toUiModel(): CouponUiModel {
    return CouponUiModel(
        id, code, description, expirationDate, discount, minimumAmount, buyQuantity, getQuantity, availableTimeStart, availableTimeEnd, discountType
    )
}