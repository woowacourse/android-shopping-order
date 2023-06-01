package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BasketProductUiModel(
    val id: Int,
    val count: CountUiModel,
    val product: ProductUiModel,
    val checked: Boolean,
) : Parcelable
