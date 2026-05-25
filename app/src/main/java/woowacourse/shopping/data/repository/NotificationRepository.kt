package woowacourse.shopping.data.repository

interface NotificationRepository {
    fun isEnabled(): Boolean

    fun setEnabled(enabled: Boolean)
}
