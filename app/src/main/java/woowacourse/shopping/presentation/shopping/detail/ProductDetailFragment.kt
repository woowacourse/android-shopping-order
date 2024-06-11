package woowacourse.shopping.presentation.shopping.detail

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
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryInjector
import woowacourse.shopping.data.shopping.ProductRepositoryInjector
import woowacourse.shopping.databinding.FragmentProductDetailBinding
import woowacourse.shopping.domain.usecase.cart.DefaultAddCartProductUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.shopping.product.ProductListFragment
import woowacourse.shopping.presentation.util.showToast

class ProductDetailFragment :
    BindingFragment<FragmentProductDetailBinding>(R.layout.fragment_product_detail) {
    private val viewModel by viewModels<ProductDetailViewModel> {
        eventBusViewModel.sendUpdateRecentProductEvent()
        val cartRepository =
            CartRepositoryInjector.cartRepository()
        val shoppingRepository =
            ProductRepositoryInjector.productRepository(requireContext().applicationContext)
        ProductDetailViewModel.factory(
            DefaultAddCartProductUseCase.instance(cartRepository, shoppingRepository),
            shoppingRepository,
        )
    }
    private val eventBusViewModel by activityViewModels<ShoppingEventBusViewModel>()
    private val navigator: ShoppingNavigator by lazy {
        requireActivity() as ShoppingNavigator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val id = arguments?.getLong(PRODUCT_ID, -1) ?: return
            viewModel.loadProduct(id)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding?.also {
            it.lifecycleOwner = viewLifecycleOwner
            it.vm = viewModel
        }
        initAppBar()
        initListeners()
        initObservers()
        initErrorEvent()
    }

    private fun initListeners() {
        binding?.btnProductCart?.setOnClickListener {
            viewModel.addCartProduct()
        }
    }

    private fun initObservers() {
        viewModel.addCartEvent.observe(viewLifecycleOwner) {
            navigator.navigateToCart(true, TAG)
        }
        viewModel.recentProductEvent.observe(viewLifecycleOwner) { id ->
            navigator.navigateToProductDetail(id, true, TAG)
        }
        viewModel.updateCartEvent.observe(viewLifecycleOwner) {
            eventBusViewModel.sendUpdateCartEvent()
        }
        eventBusViewModel.updateCartEvent.observe(viewLifecycleOwner) {
            viewModel.refreshCartProduct()
        }
    }

    private fun initErrorEvent() {
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            val errorMessage = event.toErrorMessageFrom(requireContext())
            showToast(errorMessage)
        }
    }

    private fun initAppBar() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(false)
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater,
                ) {
                    menuInflater.inflate(R.menu.detail_product_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.menu_item_close) {
                        if (viewModel.uiState.value?.isRecentProductVisible == false) {
                            val tag = ProductListFragment.TAG ?: return false
                            navigator.popBackStack(tag, false)
                        } else {
                            navigator.popBackStack()
                        }
                    }
                    return false
                }
            },
            viewLifecycleOwner,
        )
    }

    companion object {
        val TAG: String? = ProductDetailFragment::class.java.canonicalName
        const val PRODUCT_ID = "PRODUCT_ID"

        fun args(id: Long): Bundle =
            Bundle().apply {
                putLong(PRODUCT_ID, id)
            }
    }
}
