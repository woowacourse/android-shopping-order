package woowacourse.shopping.ui.order.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication.Companion.cartDataSourceImpl
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.ui.detail.DetailActivity
import woowacourse.shopping.ui.order.cart.action.CartNavigationActions
import woowacourse.shopping.ui.order.cart.action.CartNotifyingActions
import woowacourse.shopping.ui.order.cart.adapter.CartAdapter
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.order.cart.viewmodel.CartViewModelFactory
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.state.UiState

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = _binding!!

    private lateinit var adapter: CartAdapter
    private val orderViewModel by activityViewModels<OrderViewModel>()
    private val cartViewModel by viewModels<CartViewModel> {
        CartViewModelFactory(
            cartRepository = CartRepositoryImpl(cartDataSourceImpl),
            orderViewModel = orderViewModel,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpDataBinding()
        observeViewmodel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpAdapter() {
        adapter = CartAdapter(cartViewModel)
        binding.rvCart.adapter = adapter
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cartViewModel
    }

    private fun observeViewmodel() {
        cartViewModel.cartUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> showData(emptyList())
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }
        cartViewModel.cartNavigationActions.observe(viewLifecycleOwner) { cartNavigationActions ->
            cartNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is CartNavigationActions.NavigateToDetail -> navigateToDetail(action.productId)
                }
            }
        }

        cartViewModel.cartNotifyingActions.observe(viewLifecycleOwner) { cartNotifyingActions ->
            cartNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is CartNotifyingActions.NotifyCartItemDeleted -> notifyCartItemDeleted()
                }
            }
        }

        orderViewModel.allCheckBoxChecked.observe(viewLifecycleOwner) {
            cartViewModel.updateCartUiState()
        }
    }

    private fun showData(cartViewItems: List<CartViewItem>) {
        adapter.submitCartViewItems(cartViewItems.toList())
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    private fun notifyCartItemDeleted() {
        Toast.makeText(requireContext(), DELETE_ITEM_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DELETE_ITEM_MESSAGE = "장바구니에서 상품을 삭제했습니다!"

        fun newInstance(): Fragment {
            return CartFragment()
        }
    }
}
