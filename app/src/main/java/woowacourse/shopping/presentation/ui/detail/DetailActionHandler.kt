package woowacourse.shopping.presentation.ui.detail

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.base.CartCountHandler

interface DetailActionHandler : CartCountHandler {
    fun onAddToCart(detailCartProduct: DetailCartProduct)

    fun onNavigateToDetail(productId: Long)
}
