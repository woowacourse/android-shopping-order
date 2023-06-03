package woowacourse.shopping.data.remote.response.addorder

import woowacourse.shopping.data.remote.response.addorder.AddOrderErrorCode.LACK_OF_POINT
import woowacourse.shopping.data.remote.response.addorder.AddOrderErrorCode.SHORTAGE_STOCK
import woowacourse.shopping.domain.exception.AddOrderException.LackOfPointException
import woowacourse.shopping.domain.exception.AddOrderException.ShortageStockException

class AddOrderFailureException(
    message: String? = null,
    val addOrderErrorBody: AddOrderErrorBody
) : IllegalArgumentException(message) {
    fun toDomain(): RuntimeException {
        return when (AddOrderErrorCode.getErrorCodeFromNumberCode(addOrderErrorBody.errorCode)) {
            SHORTAGE_STOCK -> ShortageStockException(addOrderErrorBody.message)
            LACK_OF_POINT -> LackOfPointException(addOrderErrorBody.message)
        }
    }
}
