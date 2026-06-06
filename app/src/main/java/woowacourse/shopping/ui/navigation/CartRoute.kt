package woowacourse.shopping.ui.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import woowacourse.shopping.ui.cart.CartScreen
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.recommend.RecommendScreen
import woowacourse.shopping.ui.cart.recommend.RecommendViewModel

@Serializable
data object CartGraph

sealed interface CartRoute {
    @Serializable
    data object Cart : CartRoute

    @Serializable
    data object Recommend : CartRoute
}

fun NavGraphBuilder.cartNavGraph(
    navController: NavController,
    onNavigateToOrder: (List<Int>) -> Unit,
) {
    navigation<CartGraph>(startDestination = CartRoute.Cart) {
        composable<CartRoute.Cart> { entry ->
            val cartGraphEntry =
                remember(entry) {
                    navController.getBackStackEntry<CartGraph>()
                }
            val cartViewModel: CartViewModel =
                viewModel(
                    viewModelStoreOwner = cartGraphEntry,
                    factory = CartViewModel.Factory,
                )
            CartScreen(
                viewModel = cartViewModel,
                onClickClose = { navController.popBackStack() },
                onNavigateToRecommend = { navController.navigate(CartRoute.Recommend) },
            )
        }

        composable<CartRoute.Recommend> { entry ->
            val cartGraphEntry =
                remember(entry) {
                    navController.getBackStackEntry<CartGraph>()
                }
            val cartViewModel: CartViewModel =
                viewModel(
                    viewModelStoreOwner = cartGraphEntry,
                    factory = CartViewModel.Factory,
                )
            val recommendViewModel: RecommendViewModel =
                viewModel(factory = RecommendViewModel.Factory)
            RecommendScreen(
                cartViewModel = cartViewModel,
                recommendViewModel = recommendViewModel,
                onClickClose = { navController.popBackStack() },
                onNavigateToOrder = onNavigateToOrder,
            )
        }
    }
}
