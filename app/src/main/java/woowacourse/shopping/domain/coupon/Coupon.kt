package woowacourse.shopping.domain.coupon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coupon(
    val id: Int,
    val code: String,
    val expirationDate: String,
    val discountType: String,
    val description: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: AvailableTime?,
) : Parcelable
