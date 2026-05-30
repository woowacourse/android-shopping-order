package woowacourse.shopping.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CartGraph

sealed interface CartRoute {
    @Serializable
    data object Cart : CartRoute

    @Serializable
    data object Recommend : CartRoute
}
