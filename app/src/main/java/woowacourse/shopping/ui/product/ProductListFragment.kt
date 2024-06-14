package woowacourse.shopping.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentProductListBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.product.adapter.ProductAdapter
import woowacourse.shopping.ui.product.adapter.ProductHistoryAdapter

class ProductListFragment : Fragment() {
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private val factory: UniversalViewModelFactory = ProductListViewModel.factory()

    private val viewModel: ProductListViewModel by lazy {
        ViewModelProvider(this, factory)[ProductListViewModel::class.java]
    }

    private val productsAdapter: ProductAdapter by lazy { ProductAdapter(viewModel, viewModel) }
    private val historyAdapter: ProductHistoryAdapter by lazy { ProductHistoryAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater)
        initBinding()
        initRecyclerViewAdapter()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeNavigationShoppingCart()
        observeDetailProductDestination()
        observeProductsHistory()
        observeLoadedProducts()
        observeErrorMessage()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1000)
            viewModel.loadAll()
        }
    }

    private fun initBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun initRecyclerViewAdapter() {
        binding.productDetailList.adapter = productsAdapter
        binding.productLatestList.adapter = historyAdapter
    }

    private fun observeNavigationShoppingCart() {
        viewModel.shoppingCartDestination.observe(viewLifecycleOwner) {
            navigateToShoppingCart()
        }
    }

    private fun navigateToShoppingCart() {
        (requireActivity() as FragmentNavigator).navigateToShoppingCart()
    }

    private fun observeDetailProductDestination() {
        viewModel.detailProductDestinationId.observe(viewLifecycleOwner) { productId ->
            (requireActivity() as FragmentNavigator).navigateToProductDetail(productId)
        }
    }

    private fun observeProductsHistory() {
        viewModel.productsHistory.observe(viewLifecycleOwner) {
            historyAdapter.updateProductsHistory(it)
        }
    }

    private fun observeLoadedProducts() {
        viewModel.loadedProducts.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                productsAdapter.updateProducts(products)
                binding.shimmerProductList.stopShimmer()
            }
        }
    }

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "ProductListFragment"
    }
}
