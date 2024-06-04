package woowacourse.shopping.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val imageUrl: String,
    val quantity: Int = INIT_QUANTITY,
) : Parcelable {
    companion object {
        const val INIT_QUANTITY = 0
    }
}
