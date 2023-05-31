package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Order

interface OrderRepository {
    fun order(
        order: Order,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    )
}
