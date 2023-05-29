package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentProductModel(
    val id: Long,
    val product: ProductModel,
) : Parcelable {
    companion object {
        val errorData: RecentProductModel =
            RecentProductModel(-1L, ProductModel(-1L, "", 0, ""))
    }
}
