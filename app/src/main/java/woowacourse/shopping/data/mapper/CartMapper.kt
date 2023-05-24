package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.CartEntity2
import woowacourse.shopping.presentation.model.CartModel

fun CartEntity2.toUIModel(): CartModel =
    CartModel(
        id,
        product.toUIModel().apply { count = quantity },
        checked = true,
    )
