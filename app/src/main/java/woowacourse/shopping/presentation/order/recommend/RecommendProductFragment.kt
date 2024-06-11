package woowacourse.shopping.presentation.order.recommend

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
import woowacourse.shopping.databinding.FragmentRecommendProductBinding
import woowacourse.shopping.domain.usecase.cart.DefaultDecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.DefaultIncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.product.DefaultRecommendProductsUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.util.parcelable
import woowacourse.shopping.presentation.util.showToast

class RecommendProductFragment :
    BindingFragment<FragmentRecommendProductBinding>(R.layout.fragment_recommend_product) {
    private val viewModel by viewModels<RecommendProductViewModel> {
        val orders: List<CartProductUi> =
            arguments?.parcelable<OrderProductNavArgs>(ORDERED_PRODUCTS_KEY)?.orderProducts
                ?: emptyList()
        val cartRepository = CartRepositoryInjector.cartRepository()
        val productRepository =
            ProductRepositoryInjector.productRepository(requireContext().applicationContext)
        RecommendProductViewModel.factory(
            orders,
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
    private lateinit var adapter: RecommendProductAdapter

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
        adapter = RecommendProductAdapter(viewModel)
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
        viewModel.navigateToPaymentEvent.observe(viewLifecycleOwner) {
            navigator.navigateToPayment(it, true, TAG)
        }
    }

    private fun initErrorEvent() {
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            val errorMessage = it.toErrorMessageFrom(requireContext())
            showToast(errorMessage)
        }
    }

    companion object {
        val TAG: String? = RecommendProductFragment::class.java.canonicalName

        private const val ORDERED_PRODUCTS_KEY = "ORDERED_PRODUCTS_KEY"

        fun args(navArgs: OrderProductNavArgs): Bundle =
            Bundle().apply {
                putParcelable(ORDERED_PRODUCTS_KEY, navArgs)
            }
    }
}
