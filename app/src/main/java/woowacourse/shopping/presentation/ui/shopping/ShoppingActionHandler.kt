package woowacourse.shopping.presentation.ui.shopping

import kotlinx.coroutines.Job
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.common.ProductClickHandler

interface ShoppingActionHandler : ProductClickHandler {
    fun onRecentProductClick(recentProduct: RecentProduct)

    fun onCartClick()

    fun loadProductMore()

    fun saveRecentProduct(cartProduct: CartProduct): Job
}
