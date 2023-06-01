package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceModel(
    val value: Int,
) : Parcelable
