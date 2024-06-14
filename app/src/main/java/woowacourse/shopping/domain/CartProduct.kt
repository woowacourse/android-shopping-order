package woowacourse.shopping.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val productId: Long,
    val name: String,
    val imgUrl: String,
    val price: Long,
    var quantity: Int,
    val category: String = "fashion",
    var cartId: Long = 0,
) : Parcelable {
    fun plusQuantity() {
        quantity = quantity.plus(1)
    }

    fun minusQuantity() {
        if (quantity > 0) quantity = quantity.minus(1)
    }
}

fun CartProduct.toRecentProduct(): RecentProduct {
    return RecentProduct(
        this.productId,
        this.name,
        this.imgUrl,
        this.quantity,
        this.price,
        System.currentTimeMillis(),
        category = this.category,
        cartId = this.cartId,
    )
}
