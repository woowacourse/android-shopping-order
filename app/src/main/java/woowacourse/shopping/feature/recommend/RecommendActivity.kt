package woowacourse.shopping.feature.recommend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import kotlin.jvm.java
import woowacourse.shopping.feature.cart.CartContentId
import woowacourse.shopping.feature.productlist.ui.theme.AndroidshoppingTheme

class RecommendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val contentIds = IntentCompat.getParcelableArrayListExtra(intent, "cartContentIds", CartContentId::class.java)
            ?: emptyList()

        val viewModel: RecommendViewModel by viewModels {
            RecommendViewModel.recommendFactory(contentIds.map{ it.id})
        }

        setContent {
            AndroidshoppingTheme {
                RecommendScreen(
                    viewModel = viewModel,
                    onCloseClick = { finish() },
                )
            }
        }
    }
}
