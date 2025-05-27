package woowacourse.shopping.view.productDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.common.ResultFrom
import woowacourse.shopping.view.common.getSerializableExtraData
import woowacourse.shopping.view.common.showSnackBar

class ProductDetailActivity :
    AppCompatActivity(),
    ProductDetailClickListener {
    private val viewModel: ProductDetailViewModel by viewModels()
    private val binding: ActivityProductDetailBinding by lazy {
        ActivityProductDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productId: Long =
            intent.getSerializableExtraData(EXTRA_PRODUCT_ID) ?: run {
                binding.root.showSnackBar(getString(R.string.product_not_provided_error_message))
                return finish()
            }

        val isLastWatching: Boolean =
            intent.getSerializableExtraData(EXTRA_IS_LAST_WATCHING) ?: false
        viewModel.updateProduct(productId, isLastWatching)
        bindViewModel()
        setupObservers()
    }

    private fun bindViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.productDetailEventListener = this
    }

    private fun setupObservers() {
        viewModel.event.observe(this) { event: ProductDetailEvent ->
            handleEvent(event)
        }

        viewModel.quantity.observe(this) { quantity: Int ->
            binding.productDetailQuantityComponent.quantity = quantity
        }
    }

    private fun handleEvent(event: ProductDetailEvent) {
        @StringRes
        val messageResourceId: Int =
            when (event) {
                ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS -> R.string.product_detail_add_shopping_cart_success_message
                ProductDetailEvent.ADD_SHOPPING_CART_FAILURE -> R.string.product_detail_add_shopping_cart_error_message
                ProductDetailEvent.ADD_RECENT_WATCHING_FAILURE -> R.string.product_detail_add_recent_watching_error_message
                ProductDetailEvent.GET_RECENT_WATCHING_FAILURE -> R.string.product_detail_update_recent_watching_error_message
                ProductDetailEvent.GET_PRODUCT_FAILURE -> {
                    R.string.product_not_provided_error_message
                    return finish()
                }
            }

        binding.root.showSnackBar(getString(messageResourceId))
    }

    override fun onCloseButton() {
        val intent =
            Intent().apply {
                putExtra("updateProduct", viewModel.product.value)
            }
        setResult(ResultFrom.PRODUCT_DETAIL_BACK.RESULT_OK, intent)
        finish()
    }

    override fun onAddingToShoppingCart() {
        viewModel.addToShoppingCart()
    }

    override fun onRecentProduct(product: Product) {
        val intent =
            Intent().apply {
                putExtra("recentProduct", viewModel.recentWatchingProduct.value)
                putExtra("updateProduct", viewModel.product.value)
            }
        setResult(ResultFrom.PRODUCT_RECENT_WATCHING_CLICK.RESULT_OK, intent)
        finish()
    }

    override fun onPlusShoppingCartClick(
        product: Product,
        quantity: Int,
    ) {
        viewModel.plusQuantity()
    }

    override fun onMinusShoppingCartClick(
        product: Product,
        quantity: Int,
    ) {
        viewModel.minusQuantity()
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "woowacourse.shopping.EXTRA_PRODUCT_ID"
        private const val EXTRA_IS_LAST_WATCHING = "woowacourse.shopping.EXTRA_IS_LAST_WATCHING"

        fun newIntent(
            context: Context,
            productId: Long,
            isLastWatching: Boolean = false,
        ): Intent =
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(
                    EXTRA_PRODUCT_ID,
                    productId,
                )
                putExtra(EXTRA_IS_LAST_WATCHING, isLastWatching)
            }
    }
}
