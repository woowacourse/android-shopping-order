package woowacourse.shopping.domain.repository.order

import woowacourse.shopping.domain.model.Product

interface OrderRepository {
    fun order(cartItemIds: List<Long>)

    fun recommendedProducts(): List<Product>
}
