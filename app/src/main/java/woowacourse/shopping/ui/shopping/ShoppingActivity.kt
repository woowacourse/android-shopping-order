package woowacourse.shopping.ui.shopping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import woowacourse.shopping.ui.nav.ShoppingNavHost

class ShoppingActivity : ComponentActivity() {
    private val viewModel: ShoppingViewModel by viewModels { ShoppingViewModel.Factory }

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
