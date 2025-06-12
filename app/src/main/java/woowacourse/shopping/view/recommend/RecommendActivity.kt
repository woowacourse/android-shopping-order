package woowacourse.shopping.view.recommend

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.databinding.ActivityRecommnedBinding
import woowacourse.shopping.view.cart.CartItemType.ProductItem
import woowacourse.shopping.view.payment.CouponApplyActivity
import java.io.Serializable

class RecommendActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRecommnedBinding.inflate(layoutInflater) }
    private val viewModel: RecommendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter =
            RecommendProductsAdapter(
                object : RecommendProductItemActions {
                    override fun onPlusProductQuantity(item: RecommendProduct) {
                        viewModel.plusCartItemQuantity(item)
                    }

                    override fun onMinusProductQuantity(item: RecommendProduct) {
                        viewModel.minusCartItemQuantity(item)
                    }
                },
            )

        viewModel.addAlreadyAddedCartItems(
            intent.getSelectedCartItemsExtra()?.map { it.cartItem } ?: emptyList(),
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.recommendCartItems.adapter = adapter
        binding.onOrder = {
            startActivity(
                CouponApplyActivity.newIntent(
                    this,
                    viewModel.selectedCartItems.value as? ArrayList ?: ArrayList(),
                ),
            )
        }
        viewModel.recommendedProducts.observe(this) { adapter.submitList(it) }

        binding.recommendBackButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        onBackPressedDispatcher.addCallback {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun Intent.getSelectedCartItemsExtra(): Set<ProductItem>? =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(
                EXTRA_SELECTED_CART_ITEMS,
                HashSet::class.java,
            ) as? Set<ProductItem>
        } else {
            getSerializableExtra(EXTRA_SELECTED_CART_ITEMS) as? Set<ProductItem>
        }

    companion object {
        fun newIntent(
            context: Context,
            productItems: Set<ProductItem>,
        ): Intent {
            val intent =
                Intent(context, RecommendActivity::class.java)
                    .putExtra(EXTRA_SELECTED_CART_ITEMS, productItems as Serializable)
            return intent
        }

        private const val EXTRA_SELECTED_CART_ITEMS =
            "woowacourse.shopping.EXTRA_SELECTED_CART_ITEM_IDS"
    }
}
