package woowacourse.shopping.data.datasource.remote.order

import android.util.Log
import com.example.domain.util.CustomResult
import com.example.domain.util.CustomResult.FAIL
import com.example.domain.util.Error.Disconnect
import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.remote.api.OrderService
import woowacourse.shopping.data.remote.response.OrderResponseDto
import woowacourse.shopping.utils.enqueueUtil

class OrderDataSourceImpl(
    private val orderService: OrderService,
    private val authInfoDataSource: AuthInfoDataSource,
) : OrderDataSource {
    override fun getCoupons(
        onSuccess: (List<OrderResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        val token = authInfoDataSource.getAuthInfo() ?: "throw IllegalArgumentException()"
     
        orderService.getCoupons(token).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }

    override fun postOrder() {
    }
}
