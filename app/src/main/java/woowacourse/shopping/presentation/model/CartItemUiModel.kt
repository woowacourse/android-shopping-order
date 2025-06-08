package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.CartItem

@Parcelize
data class CartItemUiModel(
    val cartId: Long,
    val amount: Int,
    val productId: Long,
    val category: String,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val isSelected: Boolean = false,
) : Parcelable

fun CartItem.toCartItemUiModel(): CartItemUiModel =
    CartItemUiModel(
        cartId = cartId,
        amount = amount,
        productId = product.id,
        category = product.category,
        name = product.name,
        imageUrl = product.imageUrl,
        price = product.price.value,
    )

fun CartItemUiModel.toProductUiModel(): ProductUiModel =
    ProductUiModel(
        id = productId,
        name = name,
        imageUrl = imageUrl,
        price = price,
        cartId = cartId,
        amount = amount,
        category = category,
    )
