package woowacourse.shopping.ui.productdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.ui.common.DataBindingActivity
import woowacourse.shopping.ui.custom.CartCountView
import woowacourse.shopping.ui.model.ActivityResult
import woowacourse.shopping.ui.model.ProductDetailUiState

class ProductDetailActivity : DataBindingActivity<ActivityProductDetailBinding>(R.layout.activity_product_detail) {
    private val viewModel: ProductDetailViewModel by viewModels { ProductDetailViewModel.Factory }
    private val productId: Long by lazy { intent.getLongExtra(KEY_PRODUCT_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        removeSupportActionBarTitle()
        initViewBinding()
        initObservers()
        initCartQuantityView()
        handleNavigateFromProducts()
        viewModel.loadProductDetail(productId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_product_detail_close) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun removeSupportActionBarTitle() {
        supportActionBar?.title = null
    }

    private fun initViewBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.onClickHandler =
            object : OnClickHandler {
                override fun onAddCartProductClick() {
                    viewModel.updateCartProduct()
                }

                override fun onLastHistoryProductClick(productId: Long) {
                    setResult(
                        ActivityResult.PRODUCT_DETAIL_HISTORY_PRODUCT_CLICKED.code,
                        Intent().apply {
                            putExtra(ActivityResult.PRODUCT_DETAIL_HISTORY_PRODUCT_CLICKED.key, productId)
                        },
                    )
                    finish()
                }
            }
    }

    private fun initObservers() {
        viewModel.uiState.observe(this) { uiState ->
            handleHistoryProduct(uiState)
            handleCartProductAddResult(uiState)
            handleErrorMessage(uiState)
        }
    }

    private fun handleHistoryProduct(uiState: ProductDetailUiState) {
        uiState.product.let {
            viewModel.addHistoryProduct(it.productDetail)
        }
    }

    private fun handleCartProductAddResult(uiState: ProductDetailUiState) {
        uiState.isCartProductUpdateSuccess?.let { isSuccess ->
            if (isSuccess) {
                setResult(
                    ActivityResult.PRODUCT_DETAIL_CART_UPDATED.code,
                    Intent().apply {
                        putExtra(ActivityResult.PRODUCT_DETAIL_CART_UPDATED.key, productId)
                    },
                )
                Toast.makeText(this, getString(R.string.product_detail_cart_add_success), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleErrorMessage(uiState: ProductDetailUiState) {
        uiState.connectionErrorMessage?.let {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun initCartQuantityView() {
        binding.productDetailCartProductCount.setOnClickHandler(
            object : CartCountView.OnClickHandler {
                override fun onIncreaseClick() {
                    viewModel.increaseCartProductQuantity()
                }

                override fun onDecreaseClick() {
                    viewModel.decreaseCartProductQuantity()
                }
            },
        )
    }

    private fun handleNavigateFromProducts() {
        intent.getBooleanExtra(KEY_IS_NAVIGATE_FROM_HOME, false).let { isRecentProductShown ->
            if (isRecentProductShown) viewModel.loadLastHistoryProduct()
        }
    }

    interface OnClickHandler {
        fun onAddCartProductClick()

        fun onLastHistoryProductClick(productId: Long)
    }

    companion object {
        private const val KEY_PRODUCT_ID = "PRODUCT_ID"
        private const val KEY_IS_NAVIGATE_FROM_HOME = "IS_NAVIGATE_FROM_PRODUCTS"

        fun newIntent(
            context: Context,
            productId: Long,
            isRecentHistoryProductShown: Boolean = true,
        ): Intent =
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(KEY_PRODUCT_ID, productId)
                putExtra(KEY_IS_NAVIGATE_FROM_HOME, isRecentHistoryProductShown)
            }
    }
}
