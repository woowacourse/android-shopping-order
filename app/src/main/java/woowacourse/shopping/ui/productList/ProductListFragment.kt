package woowacourse.shopping.ui.productList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentProductListBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.productList.event.ProductListError
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

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ProductListError.AddShoppingCartProduct -> showToast(R.string.error_add_product_to_cart)
                is ProductListError.CountCartProductQuantity -> showToast(R.string.error_count_cart_product_quantity)
                is ProductListError.CalculateFinalPage -> showToast(R.string.error_calculate_final_page)
                is ProductListError.LoadCartProducts -> showToast(R.string.error_message_shopping_cart_products)
                is ProductListError.LoadProductHistory -> showToast(R.string.error_load_product_history)
                is ProductListError.LoadProducts -> showToast(R.string.error_load_product)
                is ProductListError.UpdateProductQuantity -> showToast(R.string.error_message_update_products_quantity_in_cart)
            }
        }
    }

    private fun showToast(@StringRes stringId: Int) {
        Toast.makeText(
            requireContext(),
            stringId,
            Toast.LENGTH_SHORT,
        ).show()
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
