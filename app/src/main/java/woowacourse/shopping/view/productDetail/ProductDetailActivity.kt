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
import woowacourse.shopping.view.showToast

class ProductDetailActivity :
    AppCompatActivity(),
    ProductDetailActionsListener {
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

        initViewModel(intent.getLongExtra(EXTRA_PRODUCT_ID, NO_SUCH_PRODUCT))
        bindViewModel()
        handleEvents()
    }

    private fun initViewModel(id: Long) {
        viewModel.loadProduct(id)
    }

    private fun bindViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.productDetailActionsListener = this
    }

    private fun handleEvents() {
        viewModel.event.observe(this) { event: ProductDetailEvent ->
            when (event) {
                ProductDetailEvent.ADD_SHOPPING_CART_SUCCESS -> {
                    showToast(R.string.product_detail_add_shopping_cart_success_message)
                    setResult(RESULT_OK)
                    finish()
                }

                ProductDetailEvent.ADD_SHOPPING_CART_FAILURE ->
                    showToast(R.string.product_detail_add_shopping_cart_error_message)

                ProductDetailEvent.LOAD_PRODUCT_FAILURE ->
                    showToast(R.string.product_detail_load_product_error_message)

                ProductDetailEvent.RECORD_RECENT_PRODUCT_FAILURE ->
                    showToast(R.string.product_detail_record_recent_products_error_message)

                ProductDetailEvent.LOAD_SHOPPING_CART_FAILURE ->
                    showToast(getString(R.string.shopping_cart_fetch_shopping_cart_error_message))
            }
        }
    }

    override fun onClose() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onAddToShoppingCart() {
        viewModel.addToShoppingCart()
    }

    override fun onPlusProductQuantity() {
        viewModel.plusProductQuantity()
    }

    override fun onMinusProductQuantity() {
        viewModel.minusProductQuantity()
    }

    override fun onSelectLatestViewedProduct() {
        val productId: Long = viewModel.latestViewedProduct.value?.id ?: error("")
        val intent =
            newIntent(this, productId).apply {
                flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
            }
        startActivity(intent)
        finish()
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "woowacourse.shopping.EXTRA_PRODUCT_ID"
        private const val NO_SUCH_PRODUCT = -1L

        fun newIntent(
            context: Context,
            productId: Long,
        ): Intent =
            Intent(context, ProductDetailActivity::class.java)
                .putExtra(EXTRA_PRODUCT_ID, productId)
    }
}
