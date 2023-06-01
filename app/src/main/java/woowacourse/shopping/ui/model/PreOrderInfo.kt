package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiPreOrderInfo = PreOrderInfo

@Parcelize
data class PreOrderInfo(
    val representativeImageUrl: String,
    val representativeExceptCount: Int,
    val representativeTitle: String,
    val OrderTotalPrice: Int
) : Parcelable
