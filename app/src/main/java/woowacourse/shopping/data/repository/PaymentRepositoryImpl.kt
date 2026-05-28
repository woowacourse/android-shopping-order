package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.PaymentApi
import woowacourse.shopping.data.remote.dto.request.OrderRequest

class PaymentRepositoryImpl(
    private val api: PaymentApi,
) : PaymentRepository {
    override suspend fun createOrder(cartItemIds: List<String>) {
        api.createOrder(
            OrderRequest(
                cartItemIds =
                    cartItemIds.map {
                        it.toLongOrNull() ?: throw IllegalArgumentException(
                            "id 값이 올바르지 않습니다.",
                        )
                    },
            ),
        )
    }
}
