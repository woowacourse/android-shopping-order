package woowacourse.shopping.data.repository

import com.example.domain.model.Coupon
import com.example.domain.model.OrderNumber
import com.example.domain.model.Receipt
import com.example.domain.model.TotalPrice
import com.example.domain.repository.OrderRepository
import com.example.domain.util.CustomResult
import woowacourse.shopping.data.datasource.remote.order.OrderDataSource
import woowacourse.shopping.data.datasource.remote.ordercomplete.OrderCompleteDataSource
import woowacourse.shopping.mapper.toDomain

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
    private val orderCompleteDataSource: OrderCompleteDataSource,
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

    override fun postOrderWithCoupon(
        cartItemIds: List<Int>,
        couponId: Int,
        onSuccess: (OrderNumber) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderDataSource.postOrderWithCoupon(
            cartItemIds,
            couponId,
            onSuccess = { onSuccess.invoke(OrderNumber(id = it.id)) },
            onFailure = { onFailure.invoke(it) },
        )
    }

    override fun postOrderWithoutCoupon(
        cartItemIds: List<Int>,
        onSuccess: (OrderNumber) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderDataSource.postOrderWithoutCoupon(
            cartItemIds,
            onSuccess = { onSuccess.invoke(OrderNumber(id = it.id)) },
            onFailure = { onFailure.invoke(it) },
        )
    }

    override fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (TotalPrice) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderDataSource.getAppliedPrice(
            totalPrice,
            couponId,
            onSuccess = { appliedTotalResponseDto ->
                onSuccess.invoke(appliedTotalResponseDto.toDomain())
            },
            onFailure = { onFailure.invoke(it) },
        )
    }

    override fun getReceipt(
        orderId: Int,
        onSuccess: (Receipt) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    ) {
        orderCompleteDataSource.getReceipt(
            orderId,
            onSuccess = { orderCompleteResponseDto ->
                // onSuccess.invoke(orderCompleteResponseDto.toDomain())
            },
            onFailure = { onFailure.invoke(it) },
        )
    }
}
