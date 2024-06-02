package woowacourse.shopping.domain.repository.order

import woowacourse.shopping.data.model.ProductData

interface OrderRepository {
    fun order(cartItemIds: List<Long>)

    fun recommendedProducts(): List<ProductData>
}
