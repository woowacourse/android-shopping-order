package woowacourse.shopping.presentation.purchase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityPurchaseBinding

class PurchaseActivity : AppCompatActivity() {
    private val binding: ActivityPurchaseBinding by lazy {
        ActivityPurchaseBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<PurchaseViewModel> {
        (application as ShoppingApplication).getPurchaseViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    companion object {
        const val CART_ITEMS_ID = "CART_ITEMS_ID"

        fun startActivity(
            context: Context,
            cartItemIds: List<Int>,
        ) {
            val intent = Intent(context, PurchaseActivity::class.java)
            intent.putExtra(CART_ITEMS_ID, cartItemIds.toIntArray())
            context.startActivity(intent)
        }
    }
}
