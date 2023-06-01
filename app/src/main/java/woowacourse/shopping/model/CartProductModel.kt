package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductModel(
    val id: Int,
    val product: ProductModel,
    val selectedCount: ProductCountModel = ProductCountModel(0),
    val isChecked: Boolean,
) : Parcelable {
    val shouldShowCounter: Boolean
        get() = selectedCount.value > 0
}
