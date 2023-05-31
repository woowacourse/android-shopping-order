package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProductUiModel(
    val productUiModel: ProductUiModel
) : Parcelable
