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
    val discount: Int = 0,
    val minimumAmount: Int = 0,
    val buyQuantity: Int = 0,
    val getQuantity: Int = 0,
    val availableTimeStart: String? = null,
    val availableTimeEnd: String? = null,
    val discountType: String,
    val isChecked: Boolean = false
): Parcelable

fun Coupon.toUiModel(): CouponUiModel {
    return CouponUiModel(
        id, code, description, expirationDate, discount ?: 0, minimumAmount ?: 0, buyQuantity ?: 0, getQuantity ?: 0, availableTimeStart, availableTimeEnd, discountType
    )
}