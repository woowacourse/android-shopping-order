package woowacourse.shopping

import android.content.Context
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
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ui.component.route.RecommendationRoute
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
                            initCheckItemIds = initCheckItemsIds ?: emptyList(),
                        ),
                )

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                RecommendationRoute(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onOrderClick = { finish() },
                    onNavigateToProductDetail = { productId ->
                        ProductDetailActivity.startActivity(
                            context = this,
                            selectedProductId = productId,
                            lastViewedProductId = null,
                        )
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    companion object {
        const val SELECTED_CART_ITEM_IDS = "selected_cart_item_ids"
        const val SELECTED_TOTAL_PRICE = "selected_total_price"

        fun startActivity(
            context: Context,
            totalPrice: Int,
            checkedIds: List<Long>,
        ) {
            val intent = Intent(context, RecommendationActivity::class.java)
            intent.putExtra(SELECTED_TOTAL_PRICE, totalPrice)
            intent.putExtra(SELECTED_CART_ITEM_IDS, checkedIds.toLongArray())
            context.startActivity(intent)
        }
    }
}
