package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductState(
    val id: Long,
    val productId: Long,
    val productImageUrl: String,
    val productName: String,
    val productPrice: Int,
    var quantity: Int,
    val isPicked: Boolean
) : Parcelable {

    companion object {
        const val MIN_COUNT_VALUE = 1
        const val MAX_COUNT_VALUE = 99
    }
}
