package woowacourse.shopping.notification

const val ACTION_SHOW_PAYMENT_REMINDER: String = "woowacourse.shopping.action.SHOW_PAYMENT_REMINDER"
const val POST_NOTIFICATIONS_PERMISSION: String = "android.permission.POST_NOTIFICATIONS"
const val EXTRA_SELECTED_PRODUCT_IDS: String = "extra_selected_product_ids"
const val EXTRA_OPEN_PAYMENT_FROM_REMINDER: String = "extra_open_payment_from_reminder"
const val PAYMENT_REMINDER_CHANNEL_ID: String = "payment_reminder_channel"
const val PAYMENT_REMINDER_CHANNEL_NAME: String = "결제 알림"
const val PAYMENT_REMINDER_NOTIFICATION_ID: Int = 1001
const val PAYMENT_REMINDER_ALARM_REQUEST_CODE: Int = 1001
const val PAYMENT_REMINDER_OPEN_REQUEST_CODE: Int = 1002
const val PAYMENT_REMINDER_DELAY_MILLIS: Long = 5L * 60L * 1000L
