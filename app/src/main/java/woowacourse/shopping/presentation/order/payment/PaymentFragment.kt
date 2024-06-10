package woowacourse.shopping.presentation.order.payment

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
import woowacourse.shopping.data.order.OrderRepositoryInjector
import woowacourse.shopping.data.shopping.ProductRepositoryInjector
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.domain.usecase.order.DefaultLoadAvailableDiscountCouponsUseCase
import woowacourse.shopping.domain.usecase.order.DefaultOrderCartProductsUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.order.recommend.OrderProductNavArgs
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.shopping.product.ProductListFragment
import woowacourse.shopping.presentation.util.parcelable
import woowacourse.shopping.presentation.util.showToast

class PaymentFragment : BindingFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private val viewModel by viewModels<PaymentViewModel> {
        val orders: List<CartProductUi> =
            arguments?.parcelable<OrderProductNavArgs>(PAYMENT_KEY)?.orderProducts
                ?: emptyList()
        val cartRepository = CartRepositoryInjector.cartRepository()
        val productRepository =
            ProductRepositoryInjector.productRepository(requireContext().applicationContext)
        val orderRepository = OrderRepositoryInjector.orderRepository()
        PaymentViewModel.factory(
            orders,
            DefaultOrderCartProductsUseCase.instance(
                productRepository,
                cartRepository,
                orderRepository,
            ),
            DefaultLoadAvailableDiscountCouponsUseCase.instance(
                productRepository,
                cartRepository,
                orderRepository,
            ),
        )
    }
    private val eventBusViewModel by activityViewModels<ShoppingEventBusViewModel>()
    private val navigator by lazy { requireActivity() as ShoppingNavigator }
    private lateinit var adapter: CouponAdapter

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
    }

    private fun initObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            adapter.submitList(uiState.coupons)
        }
        viewModel.finishOrderEvent.observe(viewLifecycleOwner) {
            showToast(getString(R.string.msg_success_payment))
            val destination = ProductListFragment.TAG ?: return@observe
            navigator.popBackStack(destination, inclusive = false)
        }
        viewModel.showOrderDialogEvent.observe(viewLifecycleOwner) {
            showOrderDialog()
        }
        viewModel.updateCartEvent.observe(viewLifecycleOwner) {
            eventBusViewModel.sendRefreshCartEvent()
            eventBusViewModel.sendUpdateCartEvent()
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            val message = event.toErrorMessageFrom(requireContext())
            showToast(message)
        }
    }

    private fun initViews() {
        adapter = CouponAdapter(viewModel)
        binding?.apply {
            rvCoupon.adapter = adapter
            rvCoupon.isVerticalScrollBarEnabled = false
        }
    }

    private fun initAppBar() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            title = "결제하기"
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

    companion object {
        val TAG: String? = PaymentFragment::class.java.canonicalName

        private const val PAYMENT_KEY = "PAYMENT_KEY"

        fun args(navArgs: OrderProductNavArgs): Bundle =
            Bundle().apply {
                putParcelable(PAYMENT_KEY, navArgs)
            }
    }
}
