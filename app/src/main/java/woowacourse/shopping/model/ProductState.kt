package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ProductState(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int
) : Parcelable
