package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceUiModel(
    val value: Int
) : Parcelable
