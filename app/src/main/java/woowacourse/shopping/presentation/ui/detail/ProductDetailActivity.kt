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
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity.Companion.EXTRA_IS_RECENT_PRODUCT_CHANGED
import woowacourse.shopping.presentation.util.EventObserver
import kotlin.properties.Delegates

class ProductDetailActivity : BindingActivity<ActivityProductDetailBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_product_detail

    private val viewModel: ProductDetailViewModel by viewModels { ViewModelFactory() }

    private var productId by Delegates.notNull<Long>()

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.detailHandler = viewModel
        binding.viewModel = viewModel
        checkIsLastViewedProduct()
        initActionBarTitle()
        fetchInitialData()
        observeLiveData()
    }

    private fun checkIsLastViewedProduct() {
        val isLastViewedProduct = intent.getBooleanExtra(EXTRA_IS_LAST_VIEWED_PRODUCT, false)
        if (!isLastViewedProduct) viewModel.loadLastProduct()
    }

    private fun initActionBarTitle() {
        title = getString(R.string.detail_title)
    }

    private fun fetchInitialData() {
        productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1L)
        if (productId == -1L) finish()
        viewModel.fetchInitialData(productId)
    }

    private fun observeLiveData() {
        observeLastProductUpdates()
        observeErrorEventUpdates()
        observeMoveEvent()
    }

    private fun observeLastProductUpdates() {
        viewModel.lastProduct.observe(this) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> binding.lastProduct = it.data
            }
        }
    }

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

    override fun onBackPressed() {
        val intent = Intent().putExtra(EXTRA_IS_RECENT_PRODUCT_CHANGED, true)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

    companion object {
        //        const val EXTRA_QUANTITY = "quantity"
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
            quantity: Int,
        ) {
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
//                putExtra(EXTRA_QUANTITY, quantity)
                activityLauncher.launch(this)
            }
        }
    }
}
