package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductUiModel(
    val cartProductId: Long,
    val productUiModel: ProductUiModel,
    val count: Int,
    val isSelected: Boolean
) : Parcelable {
    fun totalPrice(): Int = count * productUiModel.price
}
