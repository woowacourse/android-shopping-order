package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductUiModel(
    val cartProductId: Long,
    override val productUiModel: ProductUiModel,
    override val count: Int,
    val isSelected: Boolean
) : Parcelable, OrderProduct {
    fun totalPrice(): Int = count * productUiModel.price
}
