package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProductModel(
    val product: ProductModel,
    val count: Int
) : Parcelable
