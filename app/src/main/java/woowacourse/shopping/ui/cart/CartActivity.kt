package woowacourse.shopping.ui.cart

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

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CartViewModel =
                viewModel(
                    factory = CartViewModel.Factory,
                )
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                CartScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding),
                    onClickClose = { finish() },
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
