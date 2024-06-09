package woowacourse.shopping.presentation.ui.payment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.Coupon

@Parcelize
data class CouponUiModel(
    val coupon: Coupon,
    val isChecked: Boolean = false,
) : Parcelable

fun Coupon.toUiModel(): CouponUiModel {
    return CouponUiModel(
        coupon = this,
    )
}
