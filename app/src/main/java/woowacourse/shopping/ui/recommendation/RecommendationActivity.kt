package woowacourse.shopping.ui.recommendation

import android.content.Intent
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
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.cart.CartViewModel
import woowacourse.shopping.ui.cart.CartViewModelFactory
import woowacourse.shopping.ui.productdetail.ProductDetailActivity

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


            Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                CartRecommendationScreen(
                    recommendedProducts = recommendedProducts,
                    totalPrice = totalPrice,
                    totalCount = totalCount,
                    onBackClick = { finish() },
                    onOrderClick = { finish() },
                    onAddInCart = { recommendationViewModel.addToCart(it) },
                    onAdd = { id, amount -> recommendationViewModel.updateCountWithID(id, amount) },
                    onMinus = { id, amount ->
                        recommendationViewModel.updateCountWithID(
                            id,
                            amount
                        )
                    },
                    onDelete = { id -> recommendationViewModel.removeWithID(id) },
                    onItemClick = { id ->
                        val intent = Intent(this, ProductDetailActivity::class.java).apply {
                            putExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, id)
                        }
                        startActivity(intent)
                    },
                    isContainedInCart = { id -> cartState.isContain(id) },
                    itemCount = { id -> cartState.totalCountOfSpecificPurchaseProduct(id) },
                    modifier = Modifier.Companion.padding(innerPadding)
                )
            }
        }
    }
}
