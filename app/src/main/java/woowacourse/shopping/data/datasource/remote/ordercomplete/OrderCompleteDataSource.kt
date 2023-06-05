package woowacourse.shopping.data.datasource.remote.ordercomplete

import com.example.domain.util.CustomResult
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

interface OrderCompleteDataSource {

    fun getReceipt(
        onSuccess: (OrderCompleteResponseDto) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
