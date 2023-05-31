package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiPoint = Point

@Parcelize
data class Point(
    val value: Int
) : Parcelable
