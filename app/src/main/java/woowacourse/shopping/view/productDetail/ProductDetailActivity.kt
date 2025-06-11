package woowacourse.shopping.view.productDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.view.common.QuantityTarget
import woowacourse.shopping.view.common.ResultFrom
import woowacourse.shopping.view.common.getSerializableExtraData
import woowacourse.shopping.view.common.showSnackBar
import woowacourse.shopping.view.common.showToast
import woowacourse.shopping.view.product.ProductsActivity.Companion.RESULT_UPDATED_ITEM_KEY

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
        val shoppingCartId: Long? = intent.getSerializableExtraData(EXTRA_SHOPPING_CART_ID)

        val shoppingCartQuantity: Int =
            intent.getSerializableExtraData(EXTRA_SHOPPING_CART_QUANTITY) ?: 0
        viewModel.updateProduct(productId, shoppingCartQuantity, shoppingCartId)
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
        when (event) {
            ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS ->
                binding.root.showSnackBar(
                    getString(R.string.product_detail_add_shopping_cart_success_message),
                )

            ProductDetailEvent.ADD_SHOPPING_CART_FAILURE ->
                binding.root.showSnackBar(
                    getString(R.string.product_detail_add_shopping_cart_error_message),
                )

            ProductDetailEvent.ADD_RECENT_WATCHING_FAILURE ->
                binding.root.showSnackBar(
                    getString(R.string.product_detail_add_recent_watching_error_message),
                )

            ProductDetailEvent.GET_RECENT_WATCHING_FAILURE ->
                binding.root.showSnackBar(
                    getString(R.string.product_detail_update_recent_watching_error_message),
                )

            ProductDetailEvent.GET_PRODUCT_FAILURE -> {
                showToast(getString(R.string.product_not_provided_error_message))
                return finish()
            }
        }
    }

    override fun onCloseButton() {
        val intent =
            Intent().apply {
                putExtra(RESULT_UPDATED_ITEM_KEY, viewModel.product.value?.product)
            }
        setResult(ResultFrom.PRODUCT_DETAIL_BACK.RESULT_OK, intent)
        finish()
    }

    override fun onAddingToShoppingCart() {
        viewModel.addToShoppingCart()
    }

    override fun onPlusShoppingCartClick(quantityTarget: QuantityTarget) {
        viewModel.plusQuantity()
    }

    override fun onMinusShoppingCartClick(quantityTarget: QuantityTarget) {
        viewModel.minusQuantity()
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "woowacourse.shopping.EXTRA_PRODUCT_ID"
        private const val EXTRA_SHOPPING_CART_ID = "woowacourse.shopping.EXTRA_SHOPPING_CART_ID"
        private const val EXTRA_SHOPPING_CART_QUANTITY =
            "woowacourse.shopping.EXTRA_SHOPPING_CART_QUANTITY"

        fun newIntent(
            context: Context,
            productId: Long,
            shoppingCartId: Long? = 0,
            quantity: Int = 0,
        ): Intent =
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(
                    EXTRA_PRODUCT_ID,
                    productId,
                )
                putExtra(EXTRA_SHOPPING_CART_ID, shoppingCartId)
                putExtra(EXTRA_SHOPPING_CART_QUANTITY, quantity)
            }
    }
}
