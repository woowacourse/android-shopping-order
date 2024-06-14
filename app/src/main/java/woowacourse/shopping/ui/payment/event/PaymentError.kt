package woowacourse.shopping.ui.payment.event

sealed interface PaymentError {
    data object LoadOrders : PaymentError

    data object LoadCoupons : PaymentError

    data object DiscountAmount : PaymentError

    data object Order : PaymentError
}
