package woowacourse.shopping.ui.model.preorderinfo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiPreOrderInfo = PreOrderInfo

@Parcelize
data class PreOrderInfo(
    val representativeImageUrl: String,
    val representativeExceptCount: Int,
    val representativeTitle: String,
    val orderTotalPrice: Int
) : Parcelable
