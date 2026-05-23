package woowacourse.shopping.feature.fake

import woowacourse.shopping.data.repository.setting.SettingRepository

class FakeSettingRepository(
    initialPaymentNotificationEnabled: Boolean = false,
) : SettingRepository {

    private var paymentNotificationEnabled: Boolean = initialPaymentNotificationEnabled

    override suspend fun isPaymentNotificationEnabled(): Boolean = paymentNotificationEnabled

    override suspend fun setPaymentNotificationEnabled(enabled: Boolean) {
        paymentNotificationEnabled = enabled
    }
}
