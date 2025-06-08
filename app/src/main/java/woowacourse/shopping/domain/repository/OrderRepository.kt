package woowacourse.shopping.domain.repository

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface OrderRepository {
    suspend fun orderItems(checkedItems: List<Long>): Result<Unit>
}
