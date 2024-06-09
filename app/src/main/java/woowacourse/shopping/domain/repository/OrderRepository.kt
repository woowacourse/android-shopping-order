package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.result.Result

interface OrderRepository {
    suspend fun order(cartIds: List<Long>): Result<Unit>
}
