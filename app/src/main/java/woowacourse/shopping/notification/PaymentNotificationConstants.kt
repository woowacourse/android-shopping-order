package woowacourse.shopping.notification

object PaymentNotificationConstants {
    const val ACTION_PAYMENT_TIMEOUT = "woowacourse.shopping.action.PAYMENT_TIMEOUT"
    const val EXTRA_CART_ITEM_IDS = "extra_cart_item_ids"
    const val CHANNEL_ID = "payment_timeout"
    const val CHANNEL_NAME = "Payment timeout"
    const val NOTIFICATION_ID = 1001
    const val ALARM_REQUEST_CODE = 2001
    const val TIMEOUT_MILLIS = 5 * 60 * 1000L

    const val NOTIFICATION_TITLE = "아직 결제가 완료되지 않았어요"
}
