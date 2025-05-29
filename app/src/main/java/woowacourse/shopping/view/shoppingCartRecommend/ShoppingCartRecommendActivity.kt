package woowacourse.shopping.view.shoppingCartRecommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.databinding.ActivityShoppingCartRecommendBinding
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.getSerializableExtraData

class ShoppingCartRecommendActivity : AppCompatActivity() {
    private val binding: ActivityShoppingCartRecommendBinding by lazy {
        ActivityShoppingCartRecommendBinding.inflate(layoutInflater)
    }
    private val viewModel: ShoppingCartRecommendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.shoppingCartRecommendRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViewModel()
        val shoppingCartProductsToOrder: List<ShoppingCartProduct> =
            intent
                .getSerializableExtraData<Array<ShoppingCartProduct>>(
                    EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY,
                )?.toList() ?: emptyList()

        viewModel.updateShoppingCartProductsToOrder(shoppingCartProductsToOrder)
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel
    }

    companion object {
        private const val EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY =
            "woowacourse.shopping.EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY"

        fun newIntent(
            context: Context,
            shoppingCartProductsToOrder: Array<ShoppingCartProduct>,
        ): Intent =
            Intent(context, ShoppingCartRecommendActivity::class.java).apply {
                putExtra(EXTRA_SHOPPING_CART_PRODUCTS_TO_ORDER_KEY, shoppingCartProductsToOrder)
            }
    }
}
