package woowacourse.shopping.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import woowacourse.shopping.ui.setting.SettingScreen

sealed interface SettingRoute {
    @Serializable
    data object Setting : SettingRoute
}

fun NavGraphBuilder.settingNavGraph(navController: NavController) {
    composable<SettingRoute.Setting> {
        SettingScreen(
            onNavigateBack = { navController.popBackStack() },
        )
    }
}
