package woowacourse.shopping.presentation.ui.shopping

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.presentation.base.CartCountHandler

interface ShoppingActionHandler : CartCountHandler {
    fun onProductClick(cartProduct: CartProduct)

    fun onRecentProductClick(recentProduct: RecentProduct)

    fun onCartClick()

    fun loadMore()

    fun saveRecentProduct(cartProduct: CartProduct)
}
