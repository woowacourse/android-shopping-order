package woowacourse.shopping.data.datasource.remote.order

import com.example.domain.util.CustomResult
import woowacourse.shopping.data.remote.response.OrderResponseDto

interface OrderDataSource {
    fun getCoupons(
        onSuccess: (List<OrderResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrder()
}
