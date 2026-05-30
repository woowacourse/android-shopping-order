package woowacourse.shopping.fake.repository

import woowacourse.shopping.domain.repository.SettingRepository

class FakeSettingRepository(
    private var paymentPendingNotificationEnabled: Boolean = false,
    private var notificationPermissionAsked: Boolean = false,
) : SettingRepository {
    override fun isPaymentPendingNotificationEnabled(): Boolean = paymentPendingNotificationEnabled

    override fun setPaymentPendingNotificationEnabled(enabled: Boolean) {
        paymentPendingNotificationEnabled = enabled
    }

    override fun hasAskedNotificationPermission(): Boolean = notificationPermissionAsked

    override fun markNotificationPermissionAsked() {
        notificationPermissionAsked = true
    }
}
