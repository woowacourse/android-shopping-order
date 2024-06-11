package woowacourse.shopping.presentation.shopping.product

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryInjector
import woowacourse.shopping.data.shopping.ProductRepositoryInjector
import woowacourse.shopping.databinding.FragmentProductListBinding
import woowacourse.shopping.domain.usecase.cart.DefaultDecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultIncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultLoadCartUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.shopping.product.adpater.ProductAdapter
import woowacourse.shopping.presentation.shopping.product.adpater.RecentProductAdapter
import woowacourse.shopping.presentation.shopping.product.adpater.RecentProductWrapperAdapter
import woowacourse.shopping.presentation.shopping.toErrorMessageFrom
import woowacourse.shopping.presentation.util.showToast

class ProductListFragment :
    BindingFragment<FragmentProductListBinding>(R.layout.fragment_product_list) {
    private val viewModel by viewModels<ProductListViewModel> {
        val productRepository =
            ProductRepositoryInjector.productRepository(requireContext().applicationContext)
        val cartRepository =
            CartRepositoryInjector.cartRepository()
        ProductListViewModel.factory(
            productRepository,
            DefaultLoadCartUseCase.instance(cartRepository),
            DefaultIncreaseCartProductUseCase.instance(productRepository, cartRepository),
            DefaultDecreaseCartProductUseCase.instance(productRepository, cartRepository),
        )
    }

    private val eventBusViewModel by activityViewModels<ShoppingEventBusViewModel>()
    private val navigator: ShoppingNavigator by lazy {
        requireActivity() as ShoppingNavigator
    }
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }
        initAppBar()
        initViews()
        initObservers()
        initErrorEvent()
    }

    private fun initAppBar() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater,
                ) {
                    menuInflater.inflate(R.menu.shopping_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.menu_item_cart) {
                        navigator.navigateToCart(true, TAG)
                        return true
                    }
                    return false
                }
            },
            viewLifecycleOwner,
        )
    }

    private fun initViews() {
        binding?.apply {
            recentProductAdapter =
                RecentProductAdapter(listener = { viewModel.navigateToDetail(it) })
            val recentProductWrapperAdapter = RecentProductWrapperAdapter(recentProductAdapter)
            productAdapter = ProductAdapter(listener = viewModel)
            concatAdapter = ConcatAdapter(recentProductWrapperAdapter, productAdapter)
            rvProductList.adapter = concatAdapter
            rvProductList.layoutManager =
                GridLayoutManager(requireContext(), SPAN_COUNT).apply {
                    spanSizeLookup = spanSizeLookUp()
                }
        }
    }

    private fun initObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            productAdapter.submitList(it.totalProducts)
            recentProductAdapter.submitList(it.recentProducts)
        }

        viewModel.navigateToDetailEvent.observe(viewLifecycleOwner) {
            navigator.navigateToProductDetail(it, true, TAG)
        }

        eventBusViewModel.updateCartEvent.observe(viewLifecycleOwner) {
            viewModel.loadCartProducts()
        }

        eventBusViewModel.updateRecentProductEvent.observe(viewLifecycleOwner) {
            viewModel.loadRecentProducts()
        }
    }

    private fun initErrorEvent() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            val errorMessage = it.toErrorMessageFrom(requireContext())
            showToast(errorMessage)
        }
    }

    private fun spanSizeLookUp() =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getWrappedAdapterAndPosition(position).first) {
                    is RecentProductWrapperAdapter -> SPAN_COUNT
                    is ProductAdapter -> {
                        if (productAdapter.getItemViewType(
                                concatAdapter.getWrappedAdapterAndPosition(
                                    position,
                                ).second,
                            ) == ShoppingUiModel.ITEM_VIEW_TYPE_PLUS
                        ) {
                            SPAN_COUNT
                        } else {
                            1
                        }
                    }

                    else -> SPAN_COUNT
                }
            }
        }

    companion object {
        val TAG: String? = ProductListFragment::class.java.canonicalName
        private const val SPAN_COUNT = 2
    }
}
