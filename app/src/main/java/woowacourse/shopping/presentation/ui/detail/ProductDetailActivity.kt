package woowacourse.shopping.presentation.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import woowacourse.shopping.presentation.util.EventObserver
import kotlin.properties.Delegates

class ProductDetailActivity : BindingActivity<ActivityProductDetailBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_product_detail

    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModel.Companion.Factory(
            getProductId(),
            checkIsLastViewedProduct(),
        )
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.detailHandler = viewModel
        binding.viewModel = viewModel
        checkIsLastViewedProduct()
        initActionBarTitle()
        fetchInitialData()
        observeLiveDatas()
    }

    private fun checkIsLastViewedProduct() {
        val isLastViewedProduct = intent.getBooleanExtra(EXTRA_IS_LAST_VIEWED_PRODUCT, false)
        if (!isLastViewedProduct) viewModel.loadLastProduct()
    }

    private fun initActionBarTitle() {
        title = getString(R.string.detail_title)
    }

    private fun fetchInitialData() {
        cartId = intent.getLongExtra(EXTRA_CART_ID, -1L)
        productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1L)
        val quantity = intent.getIntExtra(EXTRA_QUANTITY, 0)
        if (productId == -1L) finish()
        viewModel.fetchInitialData(cartId, quantity, productId)
    }

    private fun observeLiveDatas() {
        observeLastProductUpdates()
        observeErrorEventUpdates()
        observeMoveEvent()
    }

    private fun checkIsLastViewedProduct(): Boolean = intent.getBooleanExtra(EXTRA_IS_LAST_VIEWED_PRODUCT, false)

    private fun getProductId(): Long = intent.getLongExtra(EXTRA_PRODUCT_ID, -1L)

    private fun observeErrorEventUpdates() {
        viewModel.error.observe(
            this,
            EventObserver { showToast(it.message) },
        )
    }

    private fun observeMoveEvent() {
        viewModel.moveEvent.observe(
            this,
            EventObserver {
                when (it) {
                    is FromDetailToScreen.ProductDetail -> {
                        startWithIsLastViewed(this, it.productId)
                    }

                    is FromDetailToScreen.Shopping -> {
                        ShoppingActivity.startWithNewProductQuantity(
                            this,
                            it.productId,
                            it.quantity,
                        )
                        finish()
                    }
                }
            },
        )
    }

    companion object {
        const val EXTRA_QUANTITY = "quantity"
        const val EXTRA_CART_ID = "cartId"
        const val EXTRA_PRODUCT_ID = "productId"
        const val EXTRA_NEW_PRODUCT_QUANTITY = "productQuantity"
        private const val EXTRA_IS_LAST_VIEWED_PRODUCT = "isLastViewedProduct"

        fun startWithIsLastViewed(
            context: Context,
            productId: Long,
        ) {
            Intent(context, ProductDetailActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_PRODUCT_ID, productId)
                putExtra(EXTRA_IS_LAST_VIEWED_PRODUCT, true)
                context.startActivity(this)
            }
        }

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
            productId: Long,
            cartId: Long,
            quantity: Int,
        ) {
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
                putExtra(EXTRA_CART_ID, cartId)
                putExtra(EXTRA_QUANTITY, quantity)
                activityLauncher.launch(this)
            }
        }
    }
}
