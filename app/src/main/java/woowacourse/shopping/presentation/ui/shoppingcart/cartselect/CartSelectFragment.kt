package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartSelectBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter.CartProductsAdapter
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.OrderRecommendFragment

class CartSelectFragment : BaseFragment<FragmentCartSelectBinding>() {
    override val layoutResourceId: Int get() = R.layout.fragment_cart_select

    private val viewModel: CartSelectViewModel by viewModels {
        CartSelectViewModel.factory(
            (requireContext().applicationContext as ShoppingApplication).shoppingCartRepository,
            (requireContext().applicationContext as ShoppingApplication).orderRepository,
        )
    }

    private val adapter: CartProductsAdapter by lazy { CartProductsAdapter(viewModel, viewModel) }

    override fun initViewCreated() {
        initDataBinding()
        initAdapter()
        initObserve()

        binding.tvAddToCart.setOnClickListener {
            this.parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container_view_main,
                    OrderRecommendFragment(),
                    OrderRecommendFragment.TAG,
                )
                .commit()
        }
    }

    private fun initDataBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun initAdapter() {
        binding.rvCartProducts.adapter = adapter
    }

    private fun initObserve() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            adapter.submitList(uiState.pagingCartProduct.cartList)
        }

        viewModel.message.observeEvent(viewLifecycleOwner) { message ->
            when (message) {
                is MessageProvider.DefaultErrorMessage -> showSnackbar(message.getMessage(this.requireContext()))
            }
        }
    }

    companion object {
        const val TAG = "CartSelectFragment"
    }
}
