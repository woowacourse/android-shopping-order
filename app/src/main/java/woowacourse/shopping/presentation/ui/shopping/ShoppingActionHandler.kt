package woowacourse.shopping.presentation.ui.shopping

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.base.ProductActionHandler

interface ShoppingActionHandler : ProductActionHandler {
    fun onRecentProductClick(recentProduct: RecentProduct)

    fun onCartClick()

    fun loadMore()

    fun saveRecentProduct(cartProduct: CartProduct): Job
}
