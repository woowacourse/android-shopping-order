package woowacourse.shopping.presentation.ui.detail

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.common.ProductCountHandler

interface DetailActionHandler : ProductCountHandler {
    fun onSaveCart(cartProduct: CartProduct): Job

    fun onNavigateToDetail(recentProduct: RecentProduct)

    fun saveRecentProduct(cartProduct: CartProduct): Job
}
