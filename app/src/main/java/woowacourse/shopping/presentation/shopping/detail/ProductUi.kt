package woowacourse.shopping.presentation.shopping.detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUi(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
) : Parcelable
