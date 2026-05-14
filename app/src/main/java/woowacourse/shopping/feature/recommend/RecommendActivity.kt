package woowacourse.shopping.feature.recommend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import woowacourse.shopping.feature.productlist.ui.theme.AndroidshoppingTheme

class RecommendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidshoppingTheme {
                RecommendScreen(
                    onCloseClick = { finish() },
                )
            }
        }
    }
}
