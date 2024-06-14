package woowacourse.shopping.ui.productList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.databinding.FragmentProductListBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.productList.event.ProductListEvent

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private val viewModel: ProductListViewModel by viewModels {
        ProductListViewModel.factory()
    }

    private val productsAdapter: ProductListAdapter by lazy { ProductListAdapter(viewModel, viewModel) }
    private val historyAdapter: ProductHistoryAdapter by lazy { ProductHistoryAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.productDetailList.adapter = productsAdapter
        binding.productLatestList.adapter = historyAdapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(100)
            viewModel.loadAll()
        }
    }

    private fun showSkeletonUi() {
        binding.shimmerProductList.visibility = View.VISIBLE
        binding.productDetailList.visibility = View.GONE
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        showSkeletonUi()
        observeLoadedProducts()
        observeProductHistory()
        observeEvent()
    }

    private fun observeProductHistory() {
        viewModel.productsHistory.observe(viewLifecycleOwner) {
            historyAdapter.submitList(it)
        }
    }

    private fun observeEvent() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ProductListEvent.NavigateToCart -> (requireActivity() as FragmentNavigator).navigateToShoppingCart()
                is ProductListEvent.NavigateToProductDetail ->
                    (requireActivity() as FragmentNavigator).navigateToProductDetail(event.productId)
            }
        }
    }

    private fun observeLoadedProducts() {
        viewModel.loadedProducts.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                productsAdapter.submitList(products)
                binding.productDetailList.visibility = View.VISIBLE
                binding.shimmerProductList.visibility = View.GONE
                binding.shimmerProductList.stopShimmer()
            }
        }
    }

    companion object {
        const val TAG = "ProductListFragment"
    }
}
