package woowacourse.shopping.domain.payment

import woowacourse.shopping.domain.repository.PaymentReminderScheduler
import woowacourse.shopping.domain.repository.PaymentReminderSettingsRepository

class PaymentReminderManager(
    private val settingsRepository: PaymentReminderSettingsRepository,
    private val scheduler: PaymentReminderScheduler,
) {
    fun isEnabled(): Boolean = settingsRepository.isEnabled()

    fun setEnabled(enabled: Boolean) {
        settingsRepository.setEnabled(enabled)
    }

    fun synchronize(
        selectedProductIds: Set<Long>,
        fromReminder: Boolean,
        canPostNotifications: Boolean,
    ): Boolean {
        val isEnabled = settingsRepository.isEnabled()
        if (isEnabled && !canPostNotifications) {
            settingsRepository.setEnabled(false)
            scheduler.cancel()
            return false
        }

        if (fromReminder || selectedProductIds.isEmpty() || !isEnabled) {
            scheduler.cancel()
            return isEnabled
        }

        scheduler.cancel()
        scheduler.schedule(selectedProductIds)
        return true
    }

    fun cancel() {
        scheduler.cancel()
    }
}
