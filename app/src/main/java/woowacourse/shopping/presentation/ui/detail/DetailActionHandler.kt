package woowacourse.shopping.presentation.ui.detail

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.base.CartCountHandler
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct

interface DetailActionHandler : CartCountHandler {
    fun onAddToCart(detailCartProduct: DetailCartProduct)

    fun onNavigateToDetail(recentProduct: RecentProduct)

    fun saveRecentProduct(cartProduct: CartProduct)
}
