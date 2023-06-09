package woowacourse.shopping.data.datasource.remote.orderhistory

import android.util.Log
import com.example.domain.util.CustomResult
import com.example.domain.util.CustomResult.FAIL
import com.example.domain.util.Error.DisabledDataCall
import woowacourse.shopping.data.remote.api.OrderHistoryService
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto
import woowacourse.shopping.utils.enqueueUtil

class OrderHistoryDataSourceImpl(
    private val orderHistoryService: OrderHistoryService,
) : OrderHistoryDataSource {

    override fun getOrderHistory(
        onSuccess: (List<OrderCompleteResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderHistoryService.getOrderHistory().enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailCallResponse = { onFailure.invoke(FAIL(DisabledDataCall(it))) },
            onFailInitNetwork = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }
}
