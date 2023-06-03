package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductModel(
    val isChecked: Boolean,
    val cartId: Int,
    val id: Int,
    val name: String,
    val imageUrl: String,
    var quantity: Int,
    val price: Int,
) : Parcelable
