package woowacourse.shopping.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.setting.SettingRepository
import woowacourse.shopping.feature.MainDispatcherExtension
import woowacourse.shopping.feature.fake.FakeSettingRepository

data class SettingUiState(
    val isLoading: Boolean = false,
    val isPaymentNotificationEnabled: Boolean = false,
)

class SettingViewModel(
    private val settingRepository: SettingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val isNotificationEnabled = settingRepository.isPaymentNotificationEnabled()
            _uiState.update { it.copy(isLoading = false, isPaymentNotificationEnabled = isNotificationEnabled) }
        }
    }

    fun toggleSetting(isNotificationEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            settingRepository.setPaymentNotificationEnabled(isNotificationEnabled)
            _uiState.update { it.copy(isLoading = false, isPaymentNotificationEnabled = isNotificationEnabled) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                SettingViewModel(app.settingRepository)
            }
        }
    }
}

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
