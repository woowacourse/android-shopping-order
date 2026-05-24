package woowacourse.shopping.presentation.setting.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.fake.repository.FakeSettingRepository

class SettingViewModelTest {
    @Test
    fun `초기 SharedPreferences의 알림 설정값이 false이면 초기 알림 여부도 false이다`() {
        val settingRepository = FakeSettingRepository(paymentPendingNotificationEnabled = false)

        val viewModel = SettingViewModel(settingRepository)

        assertThat(viewModel.uiState.value.isPaymentPendingNotificationEnabled).isFalse()
    }

    @Test
    fun `초기 SharedPreferences의 알림 설정값이 true이면 초기 알림 여부도 true이다`() {
        val settingRepository = FakeSettingRepository(paymentPendingNotificationEnabled = true)

        val viewModel = SettingViewModel(settingRepository)

        assertThat(viewModel.uiState.value.isPaymentPendingNotificationEnabled).isTrue()
    }
}
