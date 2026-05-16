package woowacourse.shopping.feature.productdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlin.jvm.java
import woowacourse.shopping.feature.productlist.ui.theme.AndroidshoppingTheme

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val id = intent.getLongExtra(PRODUCT_ID, 0)
        val recentProductId = intent.getLongExtra(RECENT_PRODUCT_ID, 0)

        setContent {
            AndroidshoppingTheme {
                if (id == null) {
                    ProductDetailErrorScreen(onCloseClick = { finish() })
                } else {
                    ProductDetailScreen(
                        id = id,
                        activityFinish = { finish() },
                        recentProductId = recentProductId,
                        onClickRecentButton = { id ->
                            val intent = newIntent(this, id)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        },
                    )
                }
            }
        }
    }

    companion object {
        private const val PRODUCT_ID = "product_id"
        private const val RECENT_PRODUCT_ID = "recent_product_id"

        fun newIntent(
            context: Context,
            id: Long,
            recentProductId: Long? = null,
        ): Intent = Intent(context, ProductDetailActivity::class.java)
            .putExtra(PRODUCT_ID, id)
            .putExtra(RECENT_PRODUCT_ID, recentProductId)
    }
}
