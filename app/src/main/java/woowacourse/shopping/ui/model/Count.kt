package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiCount = Count

@Parcelize
data class Count(
    val value: Int
) : Parcelable
