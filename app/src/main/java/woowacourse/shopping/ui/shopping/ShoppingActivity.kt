package woowacourse.shopping.ui.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detail.DetailActivity

class ShoppingActivity : ComponentActivity() {
    private val viewModel: ShoppingViewModel by viewModels { ShoppingViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShoppingRoute(
                viewModel = viewModel,
                onProductClick = {
                    val intent = DetailActivity.getIntent(this, it)
                    startActivity(intent)
                },
                onCartClick = { startActivity(CartActivity.getIntent(applicationContext)) },
            )
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.loadProducts()
    }
}
