package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartSelectBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.adapter.CartProductsAdapter
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.OrderRecommendFragment

class CartSelectFragment : BaseFragment<FragmentCartSelectBinding>(R.layout.fragment_cart_select) {
    private val viewModel: CartSelectViewModel by viewModels {
        CartSelectViewModel.factory(
            (requireContext().applicationContext as ShoppingApplication).shoppingCartRepository,
        )
    }

    private val adapter: CartProductsAdapter by lazy { CartProductsAdapter(viewModel, viewModel) }

    override fun initViewCreated() {
        initDataBinding()
        initAdapter()
        initObserve()
    }

    private fun initDataBinding() {
        binding.vm = viewModel
    }

    private fun initAdapter() {
        binding.rvCartProducts.adapter = adapter
    }

    private fun initObserve() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            adapter.submitList(uiState.pagingCartProduct.carts)
        }

        viewModel.message.observeEvent(viewLifecycleOwner) { message ->
            when (message) {
                is MessageProvider.DefaultErrorMessage -> showSnackBar(message.getMessage(this.requireContext()))
            }
        }

        viewModel.navigateAction.observeEvent(viewLifecycleOwner) { navigateAction ->
            when (navigateAction) {
                is CartSelectNavigateAction.NavigateToRecommend -> {
                    val bundle = Bundle()

                    bundle.putParcelableArray(
                        OrderRecommendFragment.PUT_EXTRA_CART_IDS_KEY,
                        navigateAction.orderCarts.toTypedArray(),
                    )

                    val orderRecommendFragment = OrderRecommendFragment()
                    orderRecommendFragment.arguments = bundle
                    parentFragmentManager.commit {
                        replace(R.id.fragment_container_view_main, orderRecommendFragment)
                        addToBackStack(OrderRecommendFragment.TAG)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "CartSelectFragment"
    }
}
