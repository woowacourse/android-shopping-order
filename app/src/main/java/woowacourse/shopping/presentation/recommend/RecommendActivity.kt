package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import woowacourse.shopping.presentation.recommend.ui.RecommendScreen
import woowacourse.shopping.presentation.recommend.viewmodel.RecommendEvent
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
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current

            LaunchedEffect(Unit) {
                viewModel.loadPaymentId(productIds)
                viewModel.loadRecommendProducts()
            }

            LaunchedEffect(viewModel.uiState, lifecycleOwner) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvents.collect { event ->
                        when (event) {
                            is RecommendEvent.ShowError -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            AndroidshoppingTheme {
                RecommendScreen(
                    uiState = uiState,
                    onBack = this::finish,
                    onOrderClick = {},
                    onIncrease = viewModel::increase,
                    onDecrease = viewModel::decrease,
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
