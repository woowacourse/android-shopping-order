package woowacourse.shopping.presentation.ui.detail

import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.base.CartCountHandler

interface DetailActionHandler : CartCountHandler {
    fun onAddToCart(detailCartProduct: DetailCartProduct)

    fun onNavigateToDetail(recentProduct: RecentProduct)
}
