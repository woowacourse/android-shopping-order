package woowacourse.shopping.data.datasource.remote.orderhistory

import android.util.Log
import com.example.domain.util.CustomResult
import com.example.domain.util.CustomResult.FAIL
import com.example.domain.util.Error.Disconnect
import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.remote.api.OrderHistoryService
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto
import woowacourse.shopping.utils.enqueueUtil

class OrderHistoryDataSourceImpl(
    private val orderHistoryService: OrderHistoryService,
    private val authInfoDataSource: AuthInfoDataSource,
) : OrderHistoryDataSource {
    private val token = authInfoDataSource.getAuthInfo() ?: "throw IllegalArgumentException()"

    override fun getOrderHistory(
        onSuccess: (List<OrderCompleteResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderHistoryService.getOrderHistory(token).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }
}
