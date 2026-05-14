package woowacourse.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.domain.Products
import woowacourse.shopping.ui.component.screen.CartRecommendationScreen
import woowacourse.shopping.ui.viewmodel.CartViewModel
import woowacourse.shopping.ui.viewmodel.CartViewModelFactory
import woowacourse.shopping.ui.viewmodel.RecommendationViewModel
import woowacourse.shopping.ui.viewmodel.RecommendationViewModelFactory

class RecommendationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val cartViewModel: CartViewModel =
                viewModel<CartViewModel>(
                    factory =
                        CartViewModelFactory(
                            (application as ShoppingApplication).cartRepository
                        ),
                )

            val recommendationViewModel: RecommendationViewModel =
                viewModel<RecommendationViewModel>(
                    factory =
                        RecommendationViewModelFactory(
                            cartRepository = (application as ShoppingApplication).cartRepository,
                            productRepository = (application as ShoppingApplication).productRepository,
                            recentlyViewedProductRepository = (application as ShoppingApplication).recentlyViewedProductRepository
                        )
                )

            val totalPrice by cartViewModel.totalPrice.collectAsStateWithLifecycle()
            val totalCount by cartViewModel.cartItemCount.collectAsStateWithLifecycle()
            val cartState by recommendationViewModel.allCartItems.collectAsStateWithLifecycle()
            val recommendedProducts by recommendationViewModel.recommendedProducts.collectAsStateWithLifecycle()


            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CartRecommendationScreen(
                    recommendedProducts = recommendedProducts,
                    totalPrice = totalPrice,
                    totalCount = totalCount,
                    onBackClick = { finish() },
                    onOrderClick = { /* 주문 로직 추가 */ },
                    onAddInCart = { recommendationViewModel.addToCart(it) },
                    onAdd = { id, amount -> cartViewModel.updateCountWithID(id, amount) },
                    onMinus = { id, amount -> cartViewModel.updateCountWithID(id, amount) },
                    onDelete = { id -> cartViewModel.removeWithID(id) },
                    onItemClick = { id -> /* 상세화면 이동 로직 */ },
                    isContainedInCart = { id -> cartState.isContain(id) },
                    itemCount = { id -> cartState.totalCountOfSpecificPurchaseProduct(id) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}