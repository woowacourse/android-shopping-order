package woowacourse.shopping.viewmodel.fakes

import woowacourse.shopping.data.local.NotificationSettingStorage

class FakeNotificationSettingStorage : NotificationSettingStorage {
    private var isEnabled = true

    override fun setNotificationEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    override fun isNotificationEnabled(): Boolean {
        return isEnabled
    }
}
