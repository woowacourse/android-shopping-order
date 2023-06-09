package woowacourse.shopping.model.data.repository

import com.shopping.domain.OrderPay
import com.shopping.repository.PayRepository
import woowacourse.shopping.model.data.dto.CartItemIdDTO
import woowacourse.shopping.model.data.dto.OrderPayDTO
import woowacourse.shopping.server.retrofit.PayService
import woowacourse.shopping.server.retrofit.createResponseCallback

class PayRepositoryImpl(
    private val service: PayService
) : PayRepository {

    override fun postPay(orderPay: OrderPay, onSuccess: (Long) -> Unit) {
        service.postPay(
            OrderPayDTO(orderPay.cartItemIds.map { CartItemIdDTO(it.cartItemId) }, orderPay.originalPrice, orderPay.points)
        ).enqueue(
            createResponseCallback(
                onSuccess = {
                    onSuccess(it.orderId)
                },
                onFailure = {
                    throw IllegalStateException("서버 통신(포인트)에 실패했습니다.")
                }
            )
        )
    }
}
