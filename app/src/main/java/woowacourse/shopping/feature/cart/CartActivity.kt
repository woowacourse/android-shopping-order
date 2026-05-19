package woowacourse.shopping.feature.cart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import woowacourse.shopping.feature.cart.component.CartScreen
import woowacourse.shopping.feature.recommend.RecommendActivity
import woowacourse.shopping.theme.AndroidshoppingTheme

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidshoppingTheme {
                CartScreen(
                    onCloseClick = { finish() },
                    activityFinish = { finish() },
                    onToRecommendIntent = { cartContentIds ->
                        val toRecommendIntent = Intent(this, RecommendActivity::class.java)
                        toRecommendIntent.putParcelableArrayListExtra(
                            "cartContentIds", ArrayList(cartContentIds),
                        )
                        startActivity(toRecommendIntent)
                    },
                )
            }
        }
    }
}
