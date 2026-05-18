package woowacourse.shopping.ui.productList

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.productDetail.ProductDetailActivity

class ProductListActivity : ComponentActivity() {
    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                ProductListScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    onCartClick = {
                        startActivity(CartActivity.newIntent(this))
                    },
                    onProductClick = { productId ->
                        startActivity(ProductDetailActivity.newIntent(this, productId))
                    },
                )
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refresh()
    }
}
