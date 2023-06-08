package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiEarnRate = EarnRate

@Parcelize
data class EarnRate(
    val value: Int
) : Parcelable
