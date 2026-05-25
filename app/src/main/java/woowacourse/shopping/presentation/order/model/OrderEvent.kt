package woowacourse.shopping.presentation.order.model

sealed class OrderEvent {
    abstract val message: String

    data class Success(
        override val message: String,
    ) : OrderEvent()

    data class Fail(
        override val message: String,
    ) : OrderEvent()
}
