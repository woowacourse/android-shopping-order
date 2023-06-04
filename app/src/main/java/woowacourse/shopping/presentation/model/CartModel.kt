package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartModel(
    val id: Long,
    val product: ProductModel,
    val count: Int,
    var checked: Boolean,
) : Parcelable {
    fun updateId(id: Long): CartModel {
        return copy(id = id)
    }
}
