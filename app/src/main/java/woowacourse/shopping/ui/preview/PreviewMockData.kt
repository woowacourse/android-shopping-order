package woowacourse.shopping.ui.preview

import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.ProductUiModel

object PreviewMockData {
    val cartItems =
        listOf(
            getUiCart(),
            getUiCart(
                product =
                    getUiProduct(
                        id = "2",
                        name = "커피",
                    ),
            ),
        ).toImmutableList()

    fun getUiProduct(
        id: String = "1",
        name: String = "커피",
        imageUrl: String = "",
        price: Long = 1000,
    ) = ProductUiModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        price = price,
    )

    fun getUiCart(
        product: ProductUiModel = getUiProduct(),
        quantity: Int = 1,
        totalPrice: Long = 1000,
        id: String = "100",
        isChecked: Boolean = true,
    ) = CartItemUiModel(
        product = product,
        quantity = quantity,
        totalPrice = totalPrice,
        id = id,
        isChecked = isChecked,
    )
}
