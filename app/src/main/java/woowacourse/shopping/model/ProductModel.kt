package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val id: Int,
    val name: String,
    val price: PriceModel,
    val imageUrl: String,
) : Parcelable
