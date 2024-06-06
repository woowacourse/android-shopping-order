package woowacourse.shopping.domain.repository.order

import woowacourse.shopping.domain.model.Product

interface OrderRepository {
    fun orderCartItems(cartItemIds: List<Long>)

    fun loadRecommendedProducts(): List<Product>
}
