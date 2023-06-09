package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProductUiModel(
    val productId: Long,
    val name: String,
    val price: Int,
    val count: Int,
    val imageUrl: String,
) : Parcelable
