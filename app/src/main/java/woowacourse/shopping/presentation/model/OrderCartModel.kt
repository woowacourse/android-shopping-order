package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderCartModel(
    val id: Int,
    val productModel: ProductModel,
    val count: Int
) : Parcelable
