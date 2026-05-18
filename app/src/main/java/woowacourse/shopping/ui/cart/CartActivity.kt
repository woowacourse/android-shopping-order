package woowacourse.shopping.ui.cart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.common.theme.ShoppingTheme

class CartActivity : ComponentActivity() {
    private val container by lazy {
        (application as ShoppingApplication).appContainer
    }
    val pageSize = 5

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: CartViewModel =
                        viewModel(
                            factory = CartViewModel.provideFactory(container, pageSize)
                        )

                    CartScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onBackClick = ::finish,
                        onOrderClick = ::finish,
                    )
                }
            }
        }
    }
}
