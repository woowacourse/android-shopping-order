package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.result.Response

interface OrderRepository {
    suspend fun order(cartIds: List<Long>):Response<Unit>
}
