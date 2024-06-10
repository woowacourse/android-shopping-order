package woowacourse.shopping.presentation.purchase

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.presentation.cart.CartViewModel

class PurchaseActivity : AppCompatActivity() {
    private val viewModel by viewModels<CartViewModel> {
        (application as ShoppingApplication).getPurchaseViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
    }

    companion object {
        fun startActivity(
            context: Context,
            cartItemIds: List<Int>,
        ) {
        }
    }
}
