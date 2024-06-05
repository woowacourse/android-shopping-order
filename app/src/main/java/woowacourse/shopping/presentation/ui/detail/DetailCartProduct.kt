package woowacourse.shopping.presentation.ui.detail

import woowacourse.shopping.domain.CartProduct

data class DetailCartProduct(
    val isNew: Boolean = false,
    val cartProduct: CartProduct,
)
