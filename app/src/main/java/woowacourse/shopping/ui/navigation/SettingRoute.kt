package woowacourse.shopping.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import woowacourse.shopping.ui.setting.SettingScreen
import woowacourse.shopping.ui.setting.SettingViewModel

sealed interface SettingRoute {
    @Serializable
    data object Setting : SettingRoute
}

fun NavGraphBuilder.settingNavGraph(navController: NavController) {
    composable<SettingRoute.Setting> {
        val viewModel: SettingViewModel = viewModel(factory = SettingViewModel.Factory)
        SettingScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
        )
    }
}
