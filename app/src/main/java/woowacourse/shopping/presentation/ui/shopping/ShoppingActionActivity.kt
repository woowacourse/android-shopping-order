package woowacourse.shopping.presentation.ui.shopping

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.detail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.shopping.adapter.RecentAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingViewType

class ShoppingActionActivity : BindingActivity<ActivityShoppingBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_shopping

    private val viewModel: ShoppingViewModel by viewModels { ViewModelFactory() }

    private val shoppingAdapter: ShoppingAdapter by lazy { ShoppingAdapter(viewModel) }
    private val recentAdapter: RecentAdapter by lazy { RecentAdapter(viewModel) }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun initStartView() {
        initTitle()
        initAdapter()
        initData()
        initObserver()
        initLauncher()
    }

    private fun initTitle() {
        supportActionBar?.hide()
    }

    private fun initLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    viewModel.syncProduct()
                }
                viewModel.findAllRecent()
                viewModel.getCartItemCounts()
            }
    }

    private fun initData() {
        viewModel.loadProductByOffset()
        viewModel.loadCartByOffset()
        viewModel.findAllRecent()
    }

    private fun initObserver() {
        binding.shoppingActionHandler = viewModel
        viewModel.cartProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    binding.layoutShimmer.isVisible = true
                }

                is UiState.Success -> {
                    binding.layoutShimmer.isVisible = false
                    shoppingAdapter.submitList(it.data)
                }
            }
        }
        viewModel.cartCount.observe(this) {
            binding.tvCartCount.text = it.toString()
        }
        viewModel.recentProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    recentAdapter.submitList(it.data) {
                        binding.rvRecents.scrollToPosition(0)
                    }
                }
            }
        }
        viewModel.errorHandler.observe(
            this,
            EventObserver {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
        )
        viewModel.navigateHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is NavigateUiState.ToDetail -> {
                        resultLauncher.launch(
                            ProductDetailActivity.createIntent(this, it.cartProduct),
                        )
                    }

                    is NavigateUiState.ToCart -> {
                        resultLauncher.launch(
                            CartActivity.createIntent(this),
                        )
                    }

                    else -> {}
                }
            },
        )
    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(this, GRIDLAYOUT_COL)
        layoutManager.spanSizeLookup = spanManager
        binding.rvShopping.layoutManager = layoutManager
        binding.rvShopping.adapter = shoppingAdapter
        binding.rvRecents.adapter = recentAdapter
    }

    private val spanManager =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (shoppingAdapter.getItemViewType(position) == ShoppingViewType.Product.value) {
                    ShoppingViewType.Product.span
                } else {
                    ShoppingViewType.LoadMore.span
                }
            }
        }

    companion object {
        const val GRIDLAYOUT_COL = 2
        const val EXTRA_UPDATED_PRODUCT = "updatedProduct"
        fun createIntent(context: Context): Intent {
            return Intent(context, ShoppingActionActivity::class.java)
        }
    }
}
