package woowacourse.shopping.data.source

import retrofit2.Call

interface OrderDataSource {
    fun orderItems(ids: List<Int>): Call<Unit>
}
