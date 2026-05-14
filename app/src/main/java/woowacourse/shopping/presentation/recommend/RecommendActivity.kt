package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        Log.d("Log", "RecommendActivity onCreate: ${productIds.size}")
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.loadPaymentId(productIds)
            }

            AndroidshoppingTheme {
                RecommendScreen(
                    uiState = uiState,
                    onBack = this::finish,
                    onOrderClick = {},
                    onIncrease = {},
                    onDecrease = {},
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
