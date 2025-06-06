package woowacourse.shopping.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.getSerializableExtraData

class OrderActivity : AppCompatActivity() {
    private val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }
    private lateinit var shoppingCartProductsToOrder: List<ShoppingCartProduct>
    private val viewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productsRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        shoppingCartProductsToOrder =
            intent.getSerializableExtraData<Array<ShoppingCartProduct>>(
                EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY,
            )?.toList() ?: emptyList()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
    }

    companion object {
        private const val EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY =
            "woowacourse.shopping.EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY"

        fun newIntent(
            context: Context,
            shoppingCartProductsToOrder: Array<ShoppingCartProduct>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra(EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY, shoppingCartProductsToOrder)
            }
    }
}
