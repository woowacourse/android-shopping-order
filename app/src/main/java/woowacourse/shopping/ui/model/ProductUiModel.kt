package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUiModel(
    val id: Long = 0,
    val name: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    val quantity: Int? = null,
) : Parcelable
