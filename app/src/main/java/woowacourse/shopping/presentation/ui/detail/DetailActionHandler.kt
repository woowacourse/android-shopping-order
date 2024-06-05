package woowacourse.shopping.presentation.ui.detail

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.base.CartCountHandler
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct

interface DetailActionHandler : CartCountHandler {
    fun onAddToCart(detailCartProduct: DetailCartProduct): Job

    fun onNavigateToDetail(recentProduct: RecentProduct)

    fun saveRecentProduct(cartProduct: CartProduct): Job
}
