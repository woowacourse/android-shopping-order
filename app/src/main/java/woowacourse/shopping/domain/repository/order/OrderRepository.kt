package woowacourse.shopping.domain.repository.order

import woowacourse.shopping.domain.model.Product

interface OrderRepository {
    suspend fun orderCartItems(cartItemIds: List<Long>)

    suspend fun loadRecommendedProducts(): List<Product>
}
