package woowacourse.shopping.data.datasource.remote.order

import android.util.Log
import com.example.domain.util.CustomResult
import com.example.domain.util.CustomResult.FAIL
import com.example.domain.util.Error.Disconnect
import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.remote.api.OrderService
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto
import woowacourse.shopping.utils.enqueueUtil

class OrderDataSourceImpl(
    private val orderService: OrderService,
    private val authInfoDataSource: AuthInfoDataSource,
) : OrderDataSource {
    private val token = authInfoDataSource.getAuthInfo() ?: "throw IllegalArgumentException()"
    override fun getCoupons(
        onSuccess: (List<CouponsResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderService.getCoupons(token).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }

    override fun postOrder() {
    }

    override fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (AppliedTotalResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        Log.d("123123 1", totalPrice.toString())
        Log.d("123123 2", couponId.toString())

        orderService.getAppliedPrice(token, totalPrice, couponId).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = {
                Log.d("123123 3", it)
                onFailure.invoke(FAIL(Disconnect(it)))
            },
            onError = {
                Log.d("123123 4", it.toString())
                Log.d("NETWORK_ERROR", it.toString())
            },
        )
    }
}
