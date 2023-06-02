package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointUiModel(
    val currentPoint: Int,
    val toBeExpiredPoint: Int
) : Parcelable
