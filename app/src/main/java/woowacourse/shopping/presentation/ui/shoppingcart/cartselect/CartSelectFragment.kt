package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartSelectBinding
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.ShoppingCartNavigateAction
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter.CartProductsAdapter

class CartSelectFragment : BaseFragment<FragmentCartSelectBinding>() {
    override val layoutResourceId: Int get() = R.layout.fragment_cart_select

    private val viewModel: CartSelectViewModel by viewModels {
        CartSelectViewModel.factory(ShoppingCartRepository.getInstance())
    }

    private val adapter: CartProductsAdapter by lazy { CartProductsAdapter(viewModel, viewModel) }

    override fun initViewCreated() {
        initDataBinding()
        initAdapter()
        initObserve()
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
            adapter.submitList(uiState.pagingCartProduct.cartProducts)
        }

        viewModel.message.observeEvent(viewLifecycleOwner) { message ->
            when (message) {
                is MessageProvider.DefaultErrorMessage -> showSnackbar(message.getMessage(this.requireContext()))
            }
        }

        viewModel.navigateAction.observeEvent(viewLifecycleOwner) { navigateAction ->
            when (navigateAction) {
                is CartSelectNavigateAction.NavigateToRecommend -> {
                    (activity as? ShoppingCartNavigateAction)?.navigateToOrderRecommend(
                        navigateAction.orderCartProducts,
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "CartSelectFragment"
    }
}
