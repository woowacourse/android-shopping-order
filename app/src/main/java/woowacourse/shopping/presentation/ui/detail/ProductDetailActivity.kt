package woowacourse.shopping.presentation.ui.detail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.shopping.ShoppingActionActivity.Companion.EXTRA_UPDATED_PRODUCT
import woowacourse.shopping.utils.getParcelableExtraCompat
import kotlin.concurrent.thread

class ProductDetailActivity : BindingActivity<ActivityProductDetailBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_product_detail

    private val viewModel: ProductDetailViewModel by viewModels { ViewModelFactory() }

    override fun initStartView() {
        initTitle()

        binding.detailActionHandler = viewModel
        binding.lifecycleOwner = this

        val cartProduct = intent.getParcelableExtraCompat<CartProduct>(EXTRA_CART_PRODUCT)
        initData(cartProduct)
        initObserver()
    }

    private fun initTitle() {
        title = getString(R.string.detail_title)
    }

    private fun initObserver() {
        viewModel.product.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {

                    var data = state.data
                    if(data.quantity == 0) data = data.copy(quantity = 1)
                    binding.cartProduct = data
                }
            }
        }

        viewModel.recentProduct.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    binding.recentProduct = state.data
                    binding.layoutRecent.isVisible = !(intent.getBooleanExtra(EXTRA_OVERLAY, false))
                }
            }
        }
        viewModel.errorHandler.observe(
            this,
            EventObserver {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
        )
        viewModel.cartHandler.observe(
            this,
            EventObserver {
                intent.apply {
                    putExtra(
                        EXTRA_UPDATED_PRODUCT,
                        it,
                    )
                }.run {
                    setResult(RESULT_OK, this)
                    this@ProductDetailActivity.finish()
                }
            },
        )
//        viewModel.navigateHandler.observe(
//            this,
//            EventObserver {
//                createIntent(this, it).apply {
//                    startActivity(this)
//                }
//            },
//        )
    }

    private fun initData(cartProduct: CartProduct?) {
        binding.layoutShimmer.root.isVisible = false
        viewModel.setCartProduct(cartProduct)
        viewModel.findOneRecentProduct()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "productId"
        const val EXTRA_CART_PRODUCT ="cartProduct"
        const val EXTRA_OVERLAY = "overlay"

        fun createIntent(
            context: Context,
            cartProduct: CartProduct,
        ): Intent {
            return Intent(context, ProductDetailActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_CART_PRODUCT, cartProduct)
                if (context is ProductDetailActivity) putExtra(EXTRA_OVERLAY, true)
            }
        }
    }
}
