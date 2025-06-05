package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding
import woowacourse.shopping.presentation.product.ProductQuantityHandler
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.product.catalog.event.CatalogEventHandler
import woowacourse.shopping.presentation.product.detail.CartEvent
import woowacourse.shopping.presentation.util.getArrayListExtraCompat

class RecommendActivity : AppCompatActivity() {
    private val binding: ActivityRecommendBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_recommend)
    }

    private val viewModel: RecommendViewModel by viewModels {
        RecommendViewModel.FACTORY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        setUpBinding()
        setUpAdapter()
        setOrderInfo()
        observeCartEvent()
    }

    private fun setUpScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@RecommendActivity
        }
    }

    private fun setUpAdapter() {
        binding.recyclerViewRecommendProducts.adapter =
            RecommendAdapter(
                catalogEventHandler =
                    object : CatalogEventHandler {
                        override fun onProductClick(product: ProductUiModel) {}

                        override fun onLoadButtonClick() {}

                        override fun onOpenProductQuantitySelector(product: ProductUiModel) {
                            viewModel.addProduct(product)
                        }
                    },
                quantityHandler =
                    object : ProductQuantityHandler {
                        override fun onPlusQuantity(product: ProductUiModel) {
                            viewModel.increaseQuantity(product)
                        }

                        override fun onMinusQuantity(product: ProductUiModel) {
                            viewModel.decreaseQuantity(product)
                        }
                    },
            )
    }

    private fun setOrderInfo() {
        val checkedProducts =
            intent.getArrayListExtraCompat(CHECKED_PRODUCTS_KEY, ProductUiModel::class.java)
                ?: emptyList()
        viewModel.setCheckedProducts(checkedProducts.toList())
    }

    private fun observeCartEvent() {
        viewModel.cartEvent.observe(this) { event ->
            when (event) {
                CartEvent.ADD_TO_CART_SUCCESS -> {
                    showToast(R.string.text_add_to_cart_success)
                }

                CartEvent.ADD_TO_CART_FAILURE -> {
                    showToast(R.string.text_unInserted_toast)
                }
            }
        }
    }

    private fun showToast(resId: Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CHECKED_PRODUCTS_KEY = "Products"

        fun newIntent(
            context: Context,
            products: List<ProductUiModel>,
        ): Intent {
            return Intent(context, RecommendActivity::class.java).apply {
                putParcelableArrayListExtra(CHECKED_PRODUCTS_KEY, ArrayList(products))
            }
        }
    }
}
