package woowacourse.shopping.data.local.history.repository

import woowacourse.shopping.domain.model.Cart

interface HistoryRepository {
    suspend fun getAll(): List<Cart>

    suspend fun insert(cart: Cart)

    suspend fun findLatest(): Cart?
}
