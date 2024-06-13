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
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.ui.detail.DetailActivity
import woowacourse.shopping.ui.order.cart.action.CartNavigationActions
import woowacourse.shopping.ui.order.cart.action.CartNotifyingActions.NotifyCartItemDeleted
import woowacourse.shopping.ui.order.cart.action.CartNotifyingActions.NotifyError
import woowacourse.shopping.ui.order.cart.action.CartShareActions.CheckCartViewItem
import woowacourse.shopping.ui.order.cart.action.CartShareActions.DeleteCartViewItem
import woowacourse.shopping.ui.order.cart.action.CartShareActions.MinusCartViewItemQuantity
import woowacourse.shopping.ui.order.cart.action.CartShareActions.PlusCartViewItemQuantity
import woowacourse.shopping.ui.order.cart.action.CartShareActions.UpdateNewCartViewItems
import woowacourse.shopping.ui.order.cart.adapter.CartAdapter
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem
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
        CartViewModelFactory(cartRepository = CartRepositoryImpl(remoteCartDataSource))
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
        orderViewModel.updateCurrentFragmentName(this::class.java.simpleName)
        setUpAdapter()
        setUpDataBinding()
        observeViewmodel()
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.updateCartViewItems()
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
                is UiState.Loading -> showData(List(5) { ShoppingCartViewItem.CartPlaceHolderViewItem() })
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }

        cartViewModel.cartShareActions.observe(viewLifecycleOwner) { cartShareActions ->
            cartShareActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is UpdateNewCartViewItems -> orderViewModel.updateCartViewItems(action.newCartViewItems)
                    is CheckCartViewItem -> orderViewModel.onCheckBoxClick(action.cartItemId)
                    is DeleteCartViewItem ->
                        orderViewModel.onDeleteButtonClick(
                            action.cartItemId,
                        )

                    is PlusCartViewItemQuantity -> orderViewModel.onQuantityPlusButtonClick(action.productId)
                    is MinusCartViewItemQuantity -> orderViewModel.onQuantityMinusButtonClick(action.productId)
                }
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
                    is NotifyCartItemDeleted -> notifyCartItemDeleted()
                    is NotifyError -> showError(getString(R.string.unknown_error))
                }
            }
        }

        orderViewModel.cartViewItems.observe(viewLifecycleOwner) { cartViewItems ->
            cartViewModel.updateSharedCartViewItems(cartViewItems)
        }

        orderViewModel.cartViewItems.observe(viewLifecycleOwner) { cartViewItems ->
            cartViewModel.updateIsCartEmpty(cartViewItems.isEmpty())
        }

        orderViewModel.allCheckBoxChecked.observe(viewLifecycleOwner) {
            cartViewModel.updateCartUiState()
        }
    }

    private fun showData(cartViewItems: List<ShoppingCartViewItem>) {
        adapter.submitList(cartViewItems)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    private fun notifyCartItemDeleted() {
        Toast.makeText(requireContext(), getString(R.string.deleted_cart_item), Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        fun newInstance(): Fragment {
            return CartFragment()
        }
    }
}
