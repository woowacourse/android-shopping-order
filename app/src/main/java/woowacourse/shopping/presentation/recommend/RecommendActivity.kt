package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.presentation.recommend.ui.RecommendScreen
import woowacourse.shopping.presentation.recommend.viewmodel.RecommendViewModel
import woowacourse.shopping.ui.theme.AndroidshoppingTheme

class RecommendActivity : ComponentActivity() {
    private val viewModel: RecommendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productIds = this.intent.getLongArrayExtra(INTENT_PRODUCT_IDS) ?: longArrayOf()
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.initializePaymentItems(productIds)
                viewModel.loadRecommendProducts()
            }

            AndroidshoppingTheme {
                RecommendScreen(
                    uiState = uiState,
                    onBack = this::finish,
                    onOrderClick = {},
                    onIncrease = viewModel::addItemToCart,
                    onDecrease = viewModel::removeItemFromCart,
                )
            }
        }
    }

    companion object {
        private const val INTENT_PRODUCT_IDS = "ids"

        fun newIntent(
            context: Context,
            productIds: List<Long>,
        ): Intent =
            Intent(context, RecommendActivity::class.java)
                .putExtra(INTENT_PRODUCT_IDS, productIds.toLongArray())
    }
}
