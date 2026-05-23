package woowacourse.shopping.domain.repository

interface SettingRepository {
    fun isNotificationEnabled(): Boolean

    fun setNotificationEnabled(enabled: Boolean)
}
