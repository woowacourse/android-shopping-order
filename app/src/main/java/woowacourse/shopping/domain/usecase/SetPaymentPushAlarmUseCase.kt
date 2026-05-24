package woowacourse.shopping.domain.usecase

import kotlinx.serialization.json.Json
import woowacourse.shopping.domain.repository.SettingRepository
import woowacourse.shopping.domain.scheduler.PaymentNotificationScheduler
import woowacourse.shopping.presentation.navigation.OrderItem

class SetPaymentPushAlarmUseCase(
    private val settingRepository: SettingRepository,
    private val notificationScheduler: PaymentNotificationScheduler,
) {
    operator fun invoke(
        orderItems: List<OrderItem>,
        orderAmount: Long,
    ) {
        if (!settingRepository.isPaymentPendingNotificationEnabled()) return
        val orderItemsJson = Json.encodeToString(orderItems)
        notificationScheduler.schedule(orderItemsJson, orderAmount)
    }
}
