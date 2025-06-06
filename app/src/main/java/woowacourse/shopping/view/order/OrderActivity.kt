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
import woowacourse.shopping.view.common.showSnackBar
import woowacourse.shopping.view.product.ProductsActivity

class OrderActivity : AppCompatActivity() {
    private val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }
    private lateinit var shoppingCartProductsToOrder: List<ShoppingCartProduct>
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModel.factory(
            shoppingCartProductsToOrder,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        shoppingCartProductsToOrder =
            intent.getSerializableExtraData<Array<ShoppingCartProduct>>(
                EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY,
            )?.toList() ?: emptyList()

        bindViewModel()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.event.observe(this) { event ->
            when (event) {
                OrderEvent.ORDER_SUCCESS -> {
                    binding.root.showSnackBar(getString(R.string.order_complete))
                    backToProducts()
                }

                OrderEvent.ORDER_PROCEEDING -> Unit
            }
        }
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun backToProducts() {
        Intent(this, ProductsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(this)
        }
    }

    companion object {
        private const val EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY =
            "woowacourse.shopping.EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY"

        fun newIntent(
            context: Context,
            shoppingCartProductsToOrder: List<ShoppingCartProduct>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra(
                    EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY,
                    shoppingCartProductsToOrder.toTypedArray(),
                )
            }
    }
}
