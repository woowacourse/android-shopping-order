package woowacourse.shopping.domain.coupon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailableTime(
    val start: String,
    val end: String,
) : Parcelable
