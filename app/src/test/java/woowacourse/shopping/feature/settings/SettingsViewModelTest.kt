package woowacourse.shopping.feature.settings

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.fake.FakeSettingRepository
import woowacourse.shopping.feature.setting.SettingViewModel

@ExtendWith(MainDispatcherExtension::class)
class SettingsViewModelTest {
    private lateinit var viewModel: SettingViewModel

    @Test
    fun `초기 상태는 저장소에 보관된 알림 설정값을 반영한다`() {
        // given: 저장소에 true가 저장되어 있고 설정 뷰모델이 주어진다
        viewModel = SettingViewModel(
            settingRepository = FakeSettingRepository(true),
        )

        // when: 초기 로딩을 할 떄
        viewModel.initialLoading()

        // then: 알람 설정 값이 true로 설정된다
        assertThat(viewModel.uiState.value.isPaymentNotificationEnabled).isEqualTo(true)
    }

    @Test
    fun `토글 시 알림 설정값이 저장소에 반영된다`() {
        // given: 로딩을 하고 설정이 true로 설정된다
        viewModel = SettingViewModel(
            settingRepository = FakeSettingRepository(true),
        )
        viewModel.initialLoading()

        // when: 토글을 할 때
        viewModel.toggleSetting(!(viewModel.uiState.value.isPaymentNotificationEnabled))

        // then: 다시 로딩을 하면 false가 반환된다
        viewModel.initialLoading()
        assertThat(viewModel.uiState.value.isPaymentNotificationEnabled).isEqualTo(false)
    }

    @Test
    fun `저장된 알림 설정값은 인스턴스 재생성 후에도 동일하게 노출된다`() {
        // given: 동일한 저장소를 공유하는 첫 번째 뷰모델에서 알림 설정을 true로 저장한다
        val sharedRepository = FakeSettingRepository(false)
        val firstViewModel = SettingViewModel(settingRepository = sharedRepository)
        firstViewModel.toggleSetting(true)

        // when: 동일한 저장소로 새로운 뷰모델 인스턴스를 만들어 초기 로딩을 수행한다
        val recreatedViewModel = SettingViewModel(settingRepository = sharedRepository)
        recreatedViewModel.initialLoading()

        // then: 이전에 저장된 알림 설정값(true)이 그대로 노출된다
        assertThat(recreatedViewModel.uiState.value.isPaymentNotificationEnabled).isEqualTo(true)
    }
}
