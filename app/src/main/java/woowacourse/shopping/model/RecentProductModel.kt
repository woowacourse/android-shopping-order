package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentProductModel(
    val id: Int,
    val product: ProductModel,
) : Parcelable
