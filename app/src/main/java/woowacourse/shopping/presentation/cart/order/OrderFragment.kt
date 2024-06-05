package woowacourse.shopping.presentation.cart.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryInjector
import woowacourse.shopping.data.shopping.ProductRepositoryInjector
import woowacourse.shopping.databinding.FragmentOrderProductBinding
import woowacourse.shopping.domain.usecase.DefaultDecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.DefaultIncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.DefaultOrderCartProductsUseCase
import woowacourse.shopping.domain.usecase.DefaultRecommendProductsUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.shopping.product.ProductListFragment
import woowacourse.shopping.presentation.util.parcelable
import woowacourse.shopping.presentation.util.showToast

class OrderFragment :
    BindingFragment<FragmentOrderProductBinding>(R.layout.fragment_order_product) {
    private val viewModel by viewModels<OrderViewModel> {
        val orders: List<CartProductUi> =
            arguments?.parcelable<OrderNavArgs>(ORDERED_PRODUCTS_KEY)?.orderProducts
                ?: emptyList()
        val cartRepository = CartRepositoryInjector.cartRepository()
        val productRepository =
            ProductRepositoryInjector.productRepository(requireContext().applicationContext)
        OrderViewModel.factory(
            orders,
            DefaultOrderCartProductsUseCase.instance(productRepository, cartRepository),
            DefaultDecreaseCartProductUseCase.instance(productRepository, cartRepository),
            DefaultIncreaseCartProductUseCase.instance(productRepository, cartRepository),
            DefaultRecommendProductsUseCase.instance(
                productRepository,
                cartRepository,
            ),
        )
    }

    private val eventBusViewModel by activityViewModels<ShoppingEventBusViewModel>()
    private val navigator by lazy { requireActivity() as ShoppingNavigator }
    private lateinit var adapter: OrderAdapter

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
        initObserver()
        initErrorEvent()
    }

    private fun initViews() {
        adapter = OrderAdapter(viewModel)
        binding?.rvRecommendProducts?.adapter = adapter
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

    private fun initObserver() {
        viewModel.updateCartEvent.observe(viewLifecycleOwner) {
            eventBusViewModel.sendRefreshCartEvent()
            eventBusViewModel.sendUpdateCartEvent()
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.submitList(it.recommendProducts)
        }
        viewModel.finishOrderEvent.observe(viewLifecycleOwner) {
            val destination = ProductListFragment.TAG ?: return@observe
            navigator.popBackStack(destination, inclusive = false)
        }
        viewModel.showOrderDialogEvent.observe(viewLifecycleOwner) {
            showOrderDialog()
        }
    }

    private fun showOrderDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.order_dialog_title))
            .setMessage(getString(R.string.order_dialog_message))
            .setPositiveButton(getString(R.string.order_dialog_positiveBtn)) { _, _ ->
                viewModel.orderProducts()
            }
            .setNegativeButton(getString(R.string.order_dialog_negativeBtn)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun initErrorEvent() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            when (it) {
                OrderErrorEvent.OrderProducts -> {
                    showToast(R.string.error_msg_order_products)
                }

                OrderErrorEvent.IncreaseCartProduct -> {
                    showToast(R.string.error_msg_increase_cart_count)
                }

                OrderErrorEvent.DecreaseCartProduct -> {
                    showToast(R.string.error_msg_decrease_cart_count)
                }
            }
        }
    }

    companion object {
        val TAG: String? = OrderFragment::class.java.canonicalName

        private const val ORDERED_PRODUCTS_KEY = "ORDERED_PRODUCTS_KEY"

        fun args(navArgs: OrderNavArgs): Bundle =
            Bundle().apply {
                putParcelable(ORDERED_PRODUCTS_KEY, navArgs)
            }
    }
}
