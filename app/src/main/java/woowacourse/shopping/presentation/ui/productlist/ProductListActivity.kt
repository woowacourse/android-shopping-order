package woowacourse.shopping.presentation.ui.productlist

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductListBinding
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseActivity
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.productlist.adapter.ProductHistoryListAdapter
import woowacourse.shopping.presentation.ui.productlist.adapter.ProductListAdapter
import woowacourse.shopping.presentation.ui.productlist.adapter.ProductListAdapter.Companion.PRODUCT_VIEW_TYPE
import woowacourse.shopping.presentation.ui.productlist.adapter.ProductListAdapterManager
import woowacourse.shopping.presentation.ui.shoppingcart.ShoppingCartActivity

class ProductListActivity :
    BaseActivity<ActivityProductListBinding>(R.layout.activity_product_list) {
    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModel.factory(
            ProductRepository.getInstance(),
            ShoppingCartRepository.getInstance(),
            ProductHistoryRepository.getInstance(),
        )
    }

    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(viewModel, viewModel)
    }
    private val productHistoryListAdapter: ProductHistoryListAdapter by lazy {
        ProductHistoryListAdapter(viewModel)
    }
    private var scrollPosition = RecyclerView.NO_POSITION

    override fun initCreateView() {
        initDataBinding()
        initAdapter()
        initObserve()
    }

    private fun initDataBinding() {
        binding.vm = viewModel
    }

    private fun initAdapter() {
        binding.rvProductList.adapter = productListAdapter
        binding.rvProductList.layoutManager =
            ProductListAdapterManager(this, productListAdapter, 2, PRODUCT_VIEW_TYPE)

        binding.rvProductHistoryList.adapter = productHistoryListAdapter
    }

    private fun initObserve() {
        viewModel.navigateAction.observeEvent(this) { navigateAction ->
            when (navigateAction) {
                is ProductListNavigateAction.NavigateToProductDetail -> {
                    startActivity(ProductDetailActivity.getIntent(this, navigateAction.productId))
                }

                is ProductListNavigateAction.NavigateToShoppingCart -> {
                    startActivity(ShoppingCartActivity.getIntent(this))
                }
            }
        }

        viewModel.uiState.observe(this) { state ->
            productListAdapter.updateProductList(state.pagingProduct)

            val layoutManager = binding.rvProductHistoryList.layoutManager as LinearLayoutManager
            scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

            productHistoryListAdapter.submitList(state.productHistories) {
                if (scrollPosition != RecyclerView.NO_POSITION) {
                    layoutManager.scrollToPositionWithOffset(scrollPosition, 0)
                }
            }
        }

        viewModel.message.observeEvent(this) { message ->
            when (message) {
                is MessageProvider.DefaultErrorMessage -> showToastMessage(message.getMessage(this))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePagingProduct()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProductListActivity::class.java)
        }
    }
}
