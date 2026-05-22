package woowacourse.shopping.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import woowacourse.shopping.ui.nav.ShoppingNavHost
import woowacourse.shopping.ui.shopping.ShoppingViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ShoppingViewModel by viewModels { ShoppingViewModel.Companion.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShoppingNavHost()
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.loadProducts()
    }
}
