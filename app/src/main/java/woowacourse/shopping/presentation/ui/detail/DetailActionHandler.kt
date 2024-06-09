package woowacourse.shopping.presentation.ui.detail

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct

interface DetailActionHandler : ProductCountHandler {
    fun onAddToCart(detailCartProduct: DetailCartProduct): Job

    fun onNavigateToDetail(recentProduct: RecentProduct)

    fun saveRecentProduct(cartProduct: CartProduct): Job
}
