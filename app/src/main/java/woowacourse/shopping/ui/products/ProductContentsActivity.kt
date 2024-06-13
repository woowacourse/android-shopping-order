package woowacourse.shopping.ui.products

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityProductContentsBinding
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detail.ProductDetailActivity
import woowacourse.shopping.ui.products.adapter.ProductAdapter
import woowacourse.shopping.ui.products.adapter.RecentProductAdapter
import woowacourse.shopping.ui.products.uimodel.LoadingUiModel
import woowacourse.shopping.ui.products.uimodel.ProductContentError
import woowacourse.shopping.ui.products.uimodel.ProductUiModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModelFactory
import woowacourse.shopping.ui.utils.showToastMessage
import woowacourse.shopping.ui.utils.toUiText

class ProductContentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductContentsBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private val viewModel: ProductContentsViewModel by viewModels {
        ProductContentsViewModelFactory(
            ProductRepositoryImpl(),
            RecentProductRepositoryImpl.get(RecentProductDatabase.database().recentProductDao()),
            CartRepositoryImpl(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setProductAdapter()
        setRecentProductAdapter()
        initToolbar()
        observeProductItems()
        observeRecentProductItems()
        moveToProductDetailPage()
        observeError()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCartItems()
        viewModel.loadRecentProducts()
    }

    private fun initToolbar() {
        binding.ivCart.setOnClickListener {
            CartActivity.startActivity(this)
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_contents)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setProductAdapter() {
        binding.rvProducts.itemAnimator = null
        productAdapter =
            ProductAdapter(viewModel)
        binding.rvProducts.adapter = productAdapter
    }

    private fun setRecentProductAdapter() {
        binding.rvRecentProducts.itemAnimator = null
        recentProductAdapter = RecentProductAdapter(viewModel)
        binding.rvRecentProducts.adapter = recentProductAdapter
    }

    private fun observeProductItems() {
        viewModel.productWithQuantity.observe(this) {
            if (it.isLoading) {
                val items = productAdapter.currentList.isLoading()
                productAdapter.submitList(items)
                return@observe
            }

            productAdapter.submitList(it.productWithQuantities)
        }
    }

    private fun observeRecentProductItems() {
        viewModel.recentProducts.observe(this) {
            recentProductAdapter.submitList(it)
        }
    }

    private fun moveToProductDetailPage() {
        viewModel.productDetailId.observe(this) {
            ProductDetailActivity.startActivity(this, it, true)
        }
    }

    private fun observeError() {
        viewModel.dataError.observe(this) { error ->
            when (error) {
                DataError.Network.REQUEST_TIMEOUT -> showToastMessage(error.toUiText())
                DataError.Network.NO_INTERNET -> showToastMessage(error.toUiText())
                DataError.Network.SERVER -> showToastMessage(error.toUiText())
                DataError.Network.INVALID_AUTHORIZATION -> showToastMessage(error.toUiText())
                DataError.UNKNOWN -> {
                    showToastMessage(error.toUiText())
                    finish()
                }
            }
        }

        viewModel.errorScope.observe(this) { error ->
            when (error) {
                ProductContentError.Cart -> showToastMessage(R.string.cart_error)
                ProductContentError.Product -> showToastMessage(R.string.product_error)
                ProductContentError.RecentProduct -> recentProductVisibility(false)
            }
        }
    }

    private fun recentProductVisibility(isVisible: Boolean) {
        with(binding) {
            tvRecentProductComment.isVisible = isVisible
            horizonLine.isVisible = isVisible
            rvRecentProducts.isVisible = isVisible
        }
    }
}

fun List<ProductUiModel>.isLoading() = this + LoadingUiModel + LoadingUiModel
