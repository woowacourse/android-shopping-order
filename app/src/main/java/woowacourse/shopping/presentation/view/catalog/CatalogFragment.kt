package woowacourse.shopping.presentation.view.catalog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCatalogBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.cart.CartFragment
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogAdapter
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem.CatalogType
import woowacourse.shopping.presentation.view.common.BaseFragment
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler
import woowacourse.shopping.presentation.view.detail.DetailFragment

class CatalogFragment : BaseFragment<FragmentCatalogBinding>(R.layout.fragment_catalog) {
    private val viewModel: CatalogViewModel by viewModels { CatalogViewModel.Factory }

    private val catalogEventHandler =
        object : CatalogEventHandler {
            override fun increaseQuantity(product: ProductUiModel) {
                viewModel.increaseQuantity(product)
            }

            override fun onProductClicked(product: ProductUiModel) {
                navigateToScreen(DetailFragment::class.java, DetailFragment.newBundle(product.id))
            }

            override fun onLoadMoreClicked() {
                viewModel.loadCatalog(nextPage = true)
            }
        }

    private val itemCounterEventHandler =
        object : ItemCounterEventHandler {
            override fun increaseQuantity(product: ProductUiModel) {
                viewModel.increaseQuantity(product)
            }

            override fun decreaseQuantity(product: ProductUiModel) {
                viewModel.decreaseQuantity(product)
            }
        }

    private val catalogAdapter: CatalogAdapter by lazy {
        CatalogAdapter(catalogEventHandler, itemCounterEventHandler)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initBinding()
        initObserver()
        binding.btnNavigateCart.setOnClickListener {
            navigateToScreen(CartFragment::class.java)
        }
        viewModel.loadCatalog(nextPage = false)
    }

    private fun initBinding() {
        binding.apply {
            binding.vm = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
            recyclerViewProducts.adapter = catalogAdapter
            recyclerViewProducts.layoutManager =
                GridLayoutManager(requireContext(), SPAN_COUNT).apply {
                    spanSizeLookup =
                        object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                val viewType = catalogAdapter.getItemViewType(position)
                                return when (CatalogType.entries[viewType]) {
                                    CatalogType.RECENT_PRODUCTS, CatalogType.LOAD_MORE -> SPAN_COUNT
                                    CatalogType.PRODUCT -> SINGLE_SPAN_COUNT
                                }
                            }
                        }
                }
        }
    }

    private fun initObserver() {
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    binding.layoutCatalogShimmer.startShimmer()
                } else {
                    binding.layoutCatalogShimmer.stopShimmer()
                }
            }

            items.observe(viewLifecycleOwner) { products ->
                catalogAdapter.submitList(products)
            }

            itemUpdateEvent.observe(viewLifecycleOwner) {
                catalogAdapter.updateItem(it)
            }
        }
    }

    private fun navigateToScreen(
        fragment: Class<out Fragment>,
        bundle: Bundle? = null,
    ) {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.shopping_fragment_container, fragment, bundle)
            addToBackStack(null)
        }
    }

    companion object {
        private const val SPAN_COUNT = 2
        private const val SINGLE_SPAN_COUNT = 1
    }
}
