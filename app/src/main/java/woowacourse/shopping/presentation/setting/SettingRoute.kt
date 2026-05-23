package woowacourse.shopping.presentation.setting

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import woowacourse.shopping.presentation.setting.ui.SettingScreen

@Composable
fun SettingRoute(navController: NavController) {
    SettingScreen(
        onBack = navController::popBackStack,
    )
}
