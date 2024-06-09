package woowacourse.shopping.presentation.cart.recommend

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BundleCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryInjector
import woowacourse.shopping.data.shopping.ShoppingRepositoryInjector
import woowacourse.shopping.databinding.FragmentRecommendCartProductBinding
import woowacourse.shopping.domain.RecommendProductsUseCase
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.navigation.ShoppingNavigator
import woowacourse.shopping.presentation.shopping.ShoppingEventBusViewModel
import woowacourse.shopping.presentation.shopping.product.ProductListFragment

class RecommendCartProductFragment :
    BindingFragment<FragmentRecommendCartProductBinding>(R.layout.fragment_recommend_cart_product) {
    private val viewModel by viewModels<RecommendCartProductViewModel> {
        val bundle = arguments
        val orders: List<CartProductUi> =
            if (bundle != null) {
                BundleCompat.getParcelable(
                    bundle,
                    ORDERED_PRODUCTS_KEY,
                    RecommendNavArgs::class.java,
                )?.orderProducts ?: emptyList()
            } else {
                emptyList()
            }
        val cartRepository = CartRepositoryInjector.cartRepository()
        val shoppingRepository =
            ShoppingRepositoryInjector.shoppingRepository(requireContext().applicationContext)
        RecommendCartProductViewModel.factory(
            orders,
            cartRepository,
            RecommendProductsUseCase(
                shoppingRepository,
                cartRepository,
            ),
        )
    }
    private val eventBusViewModel by activityViewModels<ShoppingEventBusViewModel>()
    private val navigator by lazy { requireActivity() as ShoppingNavigator }
    private lateinit var adapter: RecommendProductsAdapter
    private val orderDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle("주문하기")
            .setMessage("상품 주문 하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                viewModel.orderProducts()
            }
            .setNegativeButton("아니요") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

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
    }

    private fun initViews() {
        adapter = RecommendProductsAdapter(viewModel)
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
            eventBusViewModel.sendUpdateCartEvent()
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            adapter.submitList(it.recommendProducts)
        }
        viewModel.finishOrderEvent.observe(viewLifecycleOwner) {
            val destination = ProductListFragment.TAG ?: return@observe
            navigator.popBackStack(destination, inclusive = false)
        }
        viewModel.navigateToRecommendEvent.observe(viewLifecycleOwner) {
//            orderDialog.show()
            navigator.navigateToPayment(it, true, TAG)
        }
    }

    companion object {
        val TAG: String? = RecommendCartProductFragment::class.java.canonicalName

        private const val ORDERED_PRODUCTS_KEY = "ORDERED_PRODUCTS_KEY"

        fun args(navArgs: RecommendNavArgs): Bundle =
            Bundle().apply {
                putParcelable(ORDERED_PRODUCTS_KEY, navArgs)
            }
    }
}
