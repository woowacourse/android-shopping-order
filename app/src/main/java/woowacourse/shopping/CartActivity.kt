package woowacourse.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.ui.component.route.CartRoute
import woowacourse.shopping.ui.theme.AndroidshoppingTheme
import woowacourse.shopping.ui.viewmodel.CartViewModel
import woowacourse.shopping.ui.viewmodel.CartViewModelFactory

class CartActivity : ComponentActivity() {
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            viewModel =
                viewModel<CartViewModel>(
                    factory =
                        CartViewModelFactory(
                            (application as ShoppingApplication).cartRepository,
                        ),
                )

            AndroidshoppingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CartRoute(
                        viewModel = viewModel,
                        onClose = { finish() },
                        onOrderClick = { totalPrice, checkedIds ->
                            if (checkedIds.isNotEmpty()){
                                RecommendationActivity.startActivity(
                                    context = this,
                                    totalPrice = totalPrice,
                                    checkedIds = checkedIds
                                )
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::viewModel.isInitialized) viewModel.fetchCart()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
