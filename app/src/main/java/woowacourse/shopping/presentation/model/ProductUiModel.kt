package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

@Parcelize
data class ProductUiModel(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val amount: Int = 0,
) : Parcelable

fun Product.toUiModel() =
    ProductUiModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price.value,
    )

fun CartItem.toUiModel() =
    ProductUiModel(
        id = product.id,
        name = product.name,
        imageUrl = product.imageUrl,
        price = product.price.value,
        amount = amount,
    )

fun ProductUiModel.toProduct() =
    Product(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = Price(price),
    )

fun ProductUiModel.toCartItem() =
    CartItem(
        product = toProduct(),
        amount = amount,
    )
