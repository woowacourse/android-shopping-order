package woowacourse.shopping.domain.repository

interface PaymentReminderSettingsRepository {
    fun isEnabled(): Boolean

    fun setEnabled(enabled: Boolean)
}
