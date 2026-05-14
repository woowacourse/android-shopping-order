package woowacourse.shopping.feature.recommend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

        setContent {
            AndroidshoppingTheme {
                RecommendScreen(
                    onCloseClick = { finish() },
                    contentIds = contentIds.map { it.id },
                )
            }
        }
    }
}
