package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoneySaleUiModel(
    val saleBoundary: String,
    val saleAmount: String
) : Parcelable
