package woowacourse.shopping.view.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.model.ProductItemDomain

@Parcelize
data class CartItemInfo(
    val cartItemId: Int,
    val quantity: Int,
    val productId: Int,
    val category: String,
    val imageUrl: String,
    val name: String,
    val price: Int,
) : Parcelable

fun CartItemDomain.toCartItemInfo(): CartItemInfo =
    CartItemInfo(
        cartItemId = cartItemId,
        quantity = quantity,
        productId = product.id,
        category = product.category,
        imageUrl = product.imageUrl,
        name = product.name,
        price = product.price
    )

fun CartItemInfo.toCartItemDomain(): CartItemDomain =
    CartItemDomain(
        cartItemId = cartItemId,
        quantity = quantity,
        product = ProductItemDomain(
            id = productId,
            category = category,
            imageUrl = imageUrl,
            name = name,
            price = price
        )
    )
