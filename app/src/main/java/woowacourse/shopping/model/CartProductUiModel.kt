package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductUiModel(
    var cartId: Long,
    val productUiModel: ProductUiModel,
    var checked: Boolean,
) : Parcelable
