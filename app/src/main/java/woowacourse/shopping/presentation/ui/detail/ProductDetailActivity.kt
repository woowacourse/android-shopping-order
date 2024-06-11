package woowacourse.shopping.presentation.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.local.database.AppDatabase
import woowacourse.shopping.local.datasource.LocalRecentViewedDataSourceImpl
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import woowacourse.shopping.presentation.util.EventObserver
import woowacourse.shopping.remote.datasource.RemoteCartDataSourceImpl
import woowacourse.shopping.remote.datasource.RemoteProductDataSourceImpl

class ProductDetailActivity : BindingActivity<ActivityProductDetailBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_product_detail

    private val viewModel: ProductDetailViewModel by viewModels {
        val productRepository = ProductRepositoryImpl(RemoteProductDataSourceImpl())
        val cartRepository = CartRepositoryImpl(remoteCartDataSource = RemoteCartDataSourceImpl())
        val recentRepository =
            RecentProductRepositoryImpl(LocalRecentViewedDataSourceImpl(AppDatabase.instanceOrNull.recentProductDao()))

        ProductDetailViewModel.Companion.Factory(
            productRepository,
            cartRepository,
            recentRepository,
            getProductId(),
            checkIsLastViewedProduct(),
        )
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        binding.lifecycleOwner = this
        binding.detailHandler = viewModel
        binding.viewModel = viewModel

        observeErrorEventUpdates()
        observeMoveEvent()
    }

    private fun checkIsLastViewedProduct(): Boolean = intent.getBooleanExtra(EXTRA_IS_LAST_VIEWED_PRODUCT, false)

    private fun getProductId(): Long = intent.getLongExtra(EXTRA_PRODUCT_ID, -1L)

    private fun observeErrorEventUpdates() {
        viewModel.error.observe(
            this,
            EventObserver { showToast(it.messageResId) },
        )
    }

    private fun observeMoveEvent() {
        viewModel.moveEvent.observe(
            this,
            EventObserver {
                when (it) {
                    is FromDetailToScreen.ProductDetail -> {
                        ShoppingActivity.startWithNewProductQuantity(this)
                        startWithIsLastViewed(this, it.productId)
                        finish()
                    }

                    is FromDetailToScreen.ShoppingWithUpdated -> {
                        showToast(R.string.cart_complete_success_message)
                        ShoppingActivity.startWithNewProductQuantity(this, it.productId, it.quantity)
                        finish()
                    }

                    is FromDetailToScreen.Shopping -> {
                        ShoppingActivity.startWithNewProductQuantity(this)
                        finish()
                    }
                }
            },
        )
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "productId"
        const val EXTRA_NEW_PRODUCT_QUANTITY = "productQuantity"
        private const val EXTRA_IS_LAST_VIEWED_PRODUCT = "isLastViewedProduct"

        fun startWithIsLastViewed(
            context: Context,
            productId: Long,
        ) {
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
                putExtra(EXTRA_IS_LAST_VIEWED_PRODUCT, true)
                context.startActivity(this)
            }
        }

        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
            productId: Long,
        ) {
            Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
                activityLauncher.launch(this)
            }
        }
    }
}
