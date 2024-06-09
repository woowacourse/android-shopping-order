package woowacourse.shopping.domain.model

import woowacourse.shopping.ui.model.CartItemUiModel

data class CartItem(
    val id: Long,
    val quantity: Int,
    val product: Product,
) {
    companion object {
        fun CartItem.toUiModel() =
            CartItemUiModel(
                id = id,
                quantity = quantity,
                product = product,
                checked = false,
            )
    }
}
