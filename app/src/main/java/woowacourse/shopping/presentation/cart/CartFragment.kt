package woowacourse.shopping.presentation.cart

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
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.domain.usecase.cart.DefaultDecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultDeleteCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultIncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultLoadCartUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultLoadPagingCartUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.util.showToast

class CartFragment :
    BindingFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    private lateinit var adapter: CartAdapter
    private val viewModel by viewModels<CartViewModel> {
        val cartRepository =
            CartRepositoryInjector.cartRepository()
        val productRepository =
            ProductRepositoryInjector.productRepository(requireContext().applicationContext)
        CartViewModel.factory(
            cartRepository,
            DefaultIncreaseCartProductUseCase.instance(productRepository, cartRepository),
            DefaultDecreaseCartProductUseCase.instance(productRepository, cartRepository),
            DefaultDeleteCartProductUseCase.instance(productRepository, cartRepository),
            DefaultLoadCartUseCase.instance(cartRepository),
            DefaultLoadPagingCartUseCase.instance(cartRepository),
        )
    }
    private val navigator by lazy { requireActivity() as ShoppingNavigator }
    private val eventBusViewModel by activityViewModels<ShoppingEventBusViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        initAppBar()
        initViews()
        initObservers()
        initErrorEvent()
    }

    private fun initAppBar() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            title = "Cart"
            setDisplayHomeAsUpEnabled(true)
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater,
                ) {
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == android.R.id.home) {
                        navigator.popBackStack()
                        return true
                    }
                    return false
                }
            },
            viewLifecycleOwner,
        )
    }

    private fun initViews() {
        adapter = CartAdapter(viewModel)
        binding?.apply {
            rvShoppingCart.adapter = adapter
        }
    }

    private fun initObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.submitList(it.currentPageProducts)
        }
        viewModel.updateCartEvent.observe(viewLifecycleOwner) {
            eventBusViewModel.sendUpdateCartEvent()
        }
        eventBusViewModel.refreshCartEvent.observe(viewLifecycleOwner) {
            viewModel.loadTotalCartProducts()
        }
        viewModel.navigateToRecommendEvent.observe(viewLifecycleOwner) {
            navigator.navigateToRecommend(it, true, TAG)
        }
    }

    private fun initErrorEvent() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            val errorMessage = it.toErrorMessageFrom(requireContext())
            showToast(errorMessage)
        }
    }

    companion object {
        val TAG: String? = CartFragment::class.java.canonicalName
    }
}
