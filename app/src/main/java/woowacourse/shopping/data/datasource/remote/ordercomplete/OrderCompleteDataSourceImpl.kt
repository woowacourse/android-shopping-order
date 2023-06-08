package woowacourse.shopping.data.datasource.remote.ordercomplete

import android.util.Log
import com.example.domain.util.CustomResult
import com.example.domain.util.CustomResult.FAIL
import com.example.domain.util.Error.Disconnect
import woowacourse.shopping.data.remote.api.OrderCompleteService
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto
import woowacourse.shopping.utils.enqueueUtil

class OrderCompleteDataSourceImpl(
    private val orderCompleteService: OrderCompleteService,
) : OrderCompleteDataSource {

    override fun getReceipt(
        orderId: Int,
        onSuccess: (OrderCompleteResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderCompleteService.getReceipt(orderId).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }
}
