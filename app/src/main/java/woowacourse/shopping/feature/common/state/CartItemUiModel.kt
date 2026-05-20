package woowacourse.shopping.feature.common.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItemUiModel(
    val contentId: Long,
    val productUiModel: ProductUiModel,
) : Parcelable
