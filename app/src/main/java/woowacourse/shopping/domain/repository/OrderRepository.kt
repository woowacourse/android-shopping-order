package woowacourse.shopping.domain.repository

import retrofit2.Call
import retrofit2.http.Body
import woowacourse.shopping.data.remote.CartItemIds

interface OrderRepository {
    fun postOrder(
        cartItemIds: List<Int>
    ): Result<Unit>
}
