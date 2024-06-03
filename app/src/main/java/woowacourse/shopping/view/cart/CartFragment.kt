package woowacourse.shopping.view.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.view.cart.adapter.CartAdapter
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.viewmodel.CartViewModel
import woowacourse.shopping.view.detail.DetailActivity
import woowacourse.shopping.view.state.UiState

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartAdapter
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
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

    private fun setUpAdapter() {
        adapter = CartAdapter(viewModel)
        binding.rvCart.adapter = adapter
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun observeViewmodel() {
        viewModel.cartUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> showData(emptyList())
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }
        viewModel.navigateToDetail.observe(viewLifecycleOwner) { navigateToDetail ->
            navigateToDetail.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }

        viewModel.notifyDeletion.observe(viewLifecycleOwner) { notifyDeletion ->
            notifyDeletion.getContentIfNotHandled()?.let {
                alertDeletion()
            }
        }

        viewModel.notifyCanNotOrder.observe(viewLifecycleOwner) { notifyCanNotOrder ->
            notifyCanNotOrder.getContentIfNotHandled()?.let {
                alertCanNotOrder()
            }
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

    private fun alertDeletion() {
        Toast.makeText(requireContext(), DELETE_ITEM_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    private fun alertCanNotOrder() {
        Toast.makeText(requireContext(), CAN_NOT_ORDER_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DELETE_ITEM_MESSAGE = "장바구니에서 상품을 삭제했습니다!"
        private const val CAN_NOT_ORDER_MESSAGE = "최소 1개 이상의 상품을 주문해주세요!"

        fun newInstance(): Fragment {
            return CartFragment()
        }
    }
}
