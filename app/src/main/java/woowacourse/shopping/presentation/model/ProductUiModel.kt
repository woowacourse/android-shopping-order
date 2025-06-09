package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product

data class ProductUiModel(
    val cartId: Long = 0L,
    val id: Long,
    val category: String = "",
    val name: String,
    val imageUrl: String,
    val price: Long,
    val quantity: Int = 0,
)

fun Product.toProductUiModel() =
    ProductUiModel(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = price.value,
    )

fun CartItem.toProductUiModel() =
    ProductUiModel(
        cartId = cartId,
        id = product.id,
        name = product.name,
        imageUrl = product.imageUrl,
        price = product.price.value,
        quantity = quantity,
    )

fun ProductUiModel.toProduct() =
    Product(
        id = id,
        category = category,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun ProductUiModel.toCartItem() =
    CartItem(
        cartId = cartId,
        product = toProduct(),
        quantity = quantity,
    )
