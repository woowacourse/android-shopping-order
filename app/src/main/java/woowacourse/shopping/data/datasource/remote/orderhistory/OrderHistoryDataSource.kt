package woowacourse.shopping.data.datasource.remote.orderhistory

import com.example.domain.util.CustomResult
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderHistoryDataSource {

    fun getOrderHistory(
        onSuccess: (List<OrderCompleteResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
