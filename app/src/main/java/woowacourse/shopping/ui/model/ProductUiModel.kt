package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUiModel(
    val id: Int,
    val name: String,
    val price: PriceUiModel,
    val imageUrl: String,
    var basketCount: Int = 0
) : Parcelable
