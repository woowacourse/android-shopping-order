package woowacourse.shopping.data.source

import retrofit2.Response

interface OrderDataSource {
    suspend fun orderItems(ids: List<Int>): Response<Unit>
}
