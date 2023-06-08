package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartProductModel(
    val id: Int,
    val productModel: ProductModel,
    val count: Int,
    val isOrdered: Boolean = false,
) : Parcelable {
    companion object {
        fun defaultInfo(): CartProductModel = CartProductModel(
            id = 5000,
            productModel = ProductModel(
                id = 7187,
                imageUrl = "https://search.yahoo.com/search?p=comprehensam",
                name = "Rebecca Carver",
                price = 5986,
            ),
            count = 4792,
            isOrdered = false,
        )
    }
}
