package woowacourse.shopping.data.datasource.remote.order

import com.example.domain.util.CustomResult
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderDataSource {
    fun getCoupons(
        onSuccess: (List<CouponsResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrderWithCoupon(
        cartItemIds: List<Int>,
        couponId: Int,
        onSuccess: (OrderCompleteResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrderWithoutCoupon(
        cartItemIds: List<Int>,
        onSuccess: (OrderCompleteResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (AppliedTotalResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
