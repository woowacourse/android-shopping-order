package woowacourse.shopping.presentation.product

import woowacourse.shopping.presentation.uimodel.CartItemUiModel

sealed class ProductItemType {
    data class Product(
        val cartItemUiModel: CartItemUiModel,
    ) : ProductItemType()

    data object LoadMore : ProductItemType()
}
