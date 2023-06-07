package woowacourse.shopping.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentProductUiModel(
    val id: Int,
    val product: ProductUiModel
) : Parcelable
