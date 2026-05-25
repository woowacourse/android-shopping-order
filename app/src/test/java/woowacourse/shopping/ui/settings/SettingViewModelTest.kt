package woowacourse.shopping.ui.settings

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.repository.SettingRepository

class SettingViewModelTest {
    @Test
    fun `저장된 미결제 알림 설정값으로 초기 상태를 만든다`() {
        val repository = FakeSettingRepository(initialValue = true)

        val viewModel = SettingViewModel(repository)

        viewModel.uiState.value.isPaymentNotificationEnabled shouldBe true
    }

    @Test
    fun `미결제 알림 설정을 변경하면 저장소와 UI 상태를 함께 갱신한다`() {
        val repository = FakeSettingRepository(initialValue = false)
        val viewModel = SettingViewModel(repository)

        viewModel.setPaymentNotificationEnabled(true)

        repository.isPaymentNotificationEnabled() shouldBe true
        viewModel.uiState.value.isPaymentNotificationEnabled shouldBe true
    }
}

private class FakeSettingRepository(
    initialValue: Boolean,
) : SettingRepository {
    private var enabled = initialValue

    override fun isPaymentNotificationEnabled(): Boolean = enabled

    override fun setPaymentNotificationEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
