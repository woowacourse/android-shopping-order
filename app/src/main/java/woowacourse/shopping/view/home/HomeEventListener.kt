package woowacourse.shopping.view.home

import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductItemDomain

interface HomeEventListener {
    fun navigateToDetail(productId: Int)

    fun navigateToCart()

    fun loadMore()

    fun addToCart(product: ProductItemDomain)
}
