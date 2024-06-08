package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.response.Response

interface OrderRepository {
    suspend fun order(cartIds: List<Long>):Response<Unit>
}
