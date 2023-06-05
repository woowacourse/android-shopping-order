package woowacourse.shopping.data.datasource.remote.order

import com.example.domain.util.CustomResult
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto

interface OrderDataSource {
    fun getCoupons(
        onSuccess: (List<CouponsResponseDto>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrder()
    fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (AppliedTotalResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
