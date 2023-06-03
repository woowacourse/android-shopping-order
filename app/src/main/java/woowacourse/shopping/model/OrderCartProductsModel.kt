package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderCartProductsModel(val orderProducts: List<OrderCartProductModel>) : Parcelable {
    @Parcelize
    data class OrderCartProductModel(val cartId: Int, val name: String, val price: Int, val imageUrl: String, val quantity: Int) : Parcelable
}
