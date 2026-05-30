package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

sealed interface SettingRoute {
    @Serializable
    data object Setting : SettingRoute
}
