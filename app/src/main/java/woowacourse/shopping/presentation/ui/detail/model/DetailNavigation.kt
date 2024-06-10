package woowacourse.shopping.presentation.ui.detail.model

import woowacourse.shopping.domain.CartProduct

sealed interface DetailNavigation {
    data class ToDetail(val cartProduct: CartProduct) : DetailNavigation
}
