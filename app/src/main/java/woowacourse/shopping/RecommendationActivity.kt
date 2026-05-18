package woowacourse.shopping

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
import woowacourse.shopping.ui.component.screen.CartRecommendationScreen
import woowacourse.shopping.ui.viewmodel.RecommendationViewModel
import woowacourse.shopping.ui.viewmodel.RecommendationViewModelFactory

class RecommendationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val initTotalPrice = intent.getIntExtra(IntentKeys.SELECTED_TOTAL_PRICE, 0)
            val initCheckItemsIds = intent.getLongArrayExtra(IntentKeys.SELECTED_CART_ITEM_IDS)?.toList()


            val viewModel: RecommendationViewModel =
                viewModel<RecommendationViewModel>(
                    factory =
                        RecommendationViewModelFactory(
                            cartRepository = (application as ShoppingApplication).cartRepository,
                            productRepository = (application as ShoppingApplication).productRepository,
                            recentlyViewedProductRepository = (application as ShoppingApplication).recentlyViewedProductRepository,
                            initPrice = initTotalPrice,
                            initCheckItemIds = initCheckItemsIds ?: emptyList()
                        ),
                )

            val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
            val checkedIds by viewModel.checkedItemIds.collectAsStateWithLifecycle()
            val cartState by viewModel.allCartItems.collectAsStateWithLifecycle()
            val recommendedProducts by viewModel.recommendedProducts.collectAsStateWithLifecycle()

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CartRecommendationScreen(
                    recommendedProducts = recommendedProducts,
                    totalPrice = totalPrice,
                    totalCount = checkedIds.size,
                    onBackClick = { finish() },
                    onOrderClick = { finish() },
                    onAddInCart = { viewModel.addToCart(it) },
                    onAdd = { id, amount -> viewModel.updateCountWithID(id, amount) },
                    onMinus = { id, amount -> viewModel.updateCountWithID(id, amount) },
                    onDelete = { id -> viewModel.removeWithID(id) },
                    onItemClick = { product ->
                        viewModel.updateHistory(product)
                        val intent =
                            Intent(this, ProductDetailActivity::class.java).apply {
                                putExtra(IntentKeys.SELECTED_PRODUCT_ID_KEY, product.id)
                            }
                        startActivity(intent)
                    },
                    isContainedInCart = { id -> cartState.isContain(id) },
                    itemCount = { id -> cartState.totalCountOfSpecificPurchaseProduct(id) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
