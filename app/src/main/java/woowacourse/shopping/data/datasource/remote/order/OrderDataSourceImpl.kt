package woowacourse.shopping.data.datasource.remote.order

import android.util.Log
import com.example.domain.util.CustomResult
import com.example.domain.util.CustomResult.FAIL
import com.example.domain.util.Error.Disconnect
import woowacourse.shopping.data.remote.api.OrderService
import woowacourse.shopping.data.remote.request.OrderWithCouponRequestDto
import woowacourse.shopping.data.remote.request.OrderWithoutCouponRequestDto
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto
import woowacourse.shopping.utils.enqueueUtil

class OrderDataSourceImpl(
    private val orderService: OrderService,
) : OrderDataSource {
    override fun getCoupons(
        onSuccess: (List<CouponsResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderService.getCoupons().enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }

    override fun postOrderWithCoupon(
        cartItemIds: List<Int>,
        couponId: Int,
        onSuccess: (OrderCompleteResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderService.postOrderWithCoupon(
            OrderWithCouponRequestDto(
                cartItemIds = cartItemIds,
                couponId = couponId,
            ),
        ).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }

    override fun postOrderWithoutCoupon(
        cartItemIds: List<Int>,
        onSuccess: (OrderCompleteResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderService.postOrderWithoutCoupon(
            OrderWithoutCouponRequestDto(
                cartItemIds = cartItemIds,
            ),
        ).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }

    override fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (AppliedTotalResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderService.getAppliedPrice(totalPrice, couponId).enqueueUtil(
            onSuccess = { onSuccess.invoke(it) },
            onFailure = { onFailure.invoke(FAIL(Disconnect(it))) },
            onError = { Log.d("NETWORK_ERROR", it.toString()) },
        )
    }
}
