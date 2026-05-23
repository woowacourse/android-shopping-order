package woowacourse.shopping.data.source.local.setting

interface SettingDataSource {
    fun isNotificationEnabled(): Boolean

    fun setNotificationEnabled(enabled: Boolean)
}
