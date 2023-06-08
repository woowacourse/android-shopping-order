package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiCartProduct = CartProduct

@Parcelize
data class CartProduct(
    val id: Int,
    val product: UiProduct,
    val selectedCount: UiProductCount = UiProductCount(0),
    val isChecked: Boolean,
) : Parcelable {
    val shouldShowCounter: Boolean
        get() = selectedCount.value > 0
}