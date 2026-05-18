package woowacourse.shopping.presentation.common.model

import woowacourse.shopping.presentation.cart.model.CartItemUiModel

object MockModels {
    val product =
        ProductUiModel(
            id = 1L,
            name = "커피",
            imageUrl = "",
            price = 1000,
        )

    val cartItem =
        CartItemUiModel(
            product = product,
            quantity = 1,
            isSelected = true,
        )
}
