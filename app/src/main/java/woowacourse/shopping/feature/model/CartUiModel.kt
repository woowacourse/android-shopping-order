package woowacourse.shopping.feature.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartUiModel(
    val id: Int,
    val name: String = "",
    val price: Int = 0,
    val imageUrl: String = "",
    val quantity: Int = 0,
) : Parcelable
