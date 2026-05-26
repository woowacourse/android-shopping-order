@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.setting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.repository.NotificationSettingRepository

class SettingViewModelTest {
    @Test
    fun `저장된 미결제 알림 설정값으로 초기 상태를 구성한다`() {
        val viewModel =
            SettingViewModel(
                notificationSettingRepository = FakeNotificationSettingRepository(isEnabled = true),
            )

        assertEquals(true, viewModel.uiState.value.isUnpaidNotificationEnabled)
    }

    @Test
    fun `미결제 알림 설정을 변경하면 저장소와 화면 상태를 함께 갱신한다`() {
        val repository = FakeNotificationSettingRepository(isEnabled = false)
        val viewModel = SettingViewModel(notificationSettingRepository = repository)

        viewModel.setUnpaidNotificationEnabled(true)

        assertEquals(true, repository.isUnpaidNotificationEnabled())
        assertEquals(true, viewModel.uiState.value.isUnpaidNotificationEnabled)
    }

    private class FakeNotificationSettingRepository(
        private var isEnabled: Boolean,
    ) : NotificationSettingRepository {
        override fun isUnpaidNotificationEnabled(): Boolean = isEnabled

        override fun setUnpaidNotificationEnabled(isEnabled: Boolean) {
            this.isEnabled = isEnabled
        }
    }
}
