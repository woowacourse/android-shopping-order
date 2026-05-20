package woowacourse.shopping.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import woowacourse.shopping.ui.cart.CartActivity

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val id = intent.getStringExtra(PRODUCT_ID)

        if (id == null) {
            Toast.makeText(this, "유효하지 않은 상품입니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            DetailRoute(
                onDismiss = { finish() },
                onRecentItemClick = { id ->
                    startActivity(getIntent(this, id, hideRecentItem = true))
                    finish()
                },
                navigateToCart = {
                    startActivity(CartActivity.getIntent(this@DetailActivity))
                },
                showToastMessage = {
                    Toast
                        .makeText(
                            this@DetailActivity,
                            it,
                            Toast.LENGTH_SHORT,
                        ).show()
                },
            )
        }
    }

    companion object {
        private const val PRODUCT_ID = "id"
        private const val HIDE_RECENT_ITEM = "hide_recent_item"

        fun getIntent(
            context: Context,
            id: String,
            hideRecentItem: Boolean = false,
        ): Intent =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, id)
                putExtra(HIDE_RECENT_ITEM, hideRecentItem)
            }
    }
}
