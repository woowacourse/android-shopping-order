package woowacourse.shopping.ui.order.event

sealed interface OrderError {
    data object LoadRecommendedProducts : OrderError

    data object CalculateOrderItemsQuantity : OrderError

    data object CalculateOrderItemsTotalPrice : OrderError

    data object UpdateOrderItem : OrderError
}
