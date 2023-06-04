package woowacourse.shopping.data.repository

import com.example.domain.model.Coupon
import com.example.domain.repository.OrderRepository
import com.example.domain.util.CustomResult
import woowacourse.shopping.data.datasource.remote.order.OrderDataSource
import woowacourse.shopping.mapper.toDomain

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository {
    override fun getCoupons(
        onSuccess: (List<Coupon>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderDataSource.getCoupons(
            onSuccess = { orderResponseDto ->
                onSuccess.invoke(orderResponseDto.map { it.toDomain() })
            },
            onFailure = { onFailure.invoke(it) },
        )
    }

    override fun postOrder() {}
}
