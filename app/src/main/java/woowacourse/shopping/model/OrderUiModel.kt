package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderUiModel(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
) : Parcelable
