package woowacourse.shopping.ui.mapper

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.CartItemUiModel

object CartItemMapper {
    fun CartItem.toUiModel() =
        CartItemUiModel(
            id = id,
            quantity = quantity,
            product = product,
            checked = false,
        )

    fun CartItemUiModel.toDomain() =
        CartItem(
            id = id,
            quantity = quantity,
            product = product,
        )
}