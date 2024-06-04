package woowacourse.shopping.view.cart.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.view.cart.CartViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = _binding!!
    private val viewModel by activityViewModels<CartViewModel>()
    private lateinit var adapter: CartAdapter

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
        binding.viewModel = viewModel

        viewModel.cartListUiState.observe(viewLifecycleOwner) { state ->
            showData(state.cartViewItems)
        }

//        viewModel.cartUiState.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is UIState.Success -> showData(state.data)
//                is UIState.Loading -> return@observe
//                is UIState.Error ->
//                    showError(
//                        state.exception.message ?: getString(R.string.unknown_error),
//                    )
//            }
//        }
//
//        viewModel.updatedCartItem.observe(viewLifecycleOwner) { cartItem ->
//            adapter.updateCartItemQuantity(cartItem)
//        }
//
//        viewModel.notifyDeletion.observe(viewLifecycleOwner) {
//            it.getContentIfNotHandled()?.let {
//                alertDeletion()
//            }
//        }
//
//        viewModel.selectChangeId.observe(viewLifecycleOwner) {
//            adapter.updateSelection(it)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showData(data: List<ShoppingCartViewItem.CartViewItem>) {
        adapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun alertDeletion() {
        Toast.makeText(requireContext(), DELETE_ITEM_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    private fun setUpAdapter() {
        adapter = CartAdapter(viewModel, viewModel)
        binding.rvCart.adapter = adapter
        binding.rvCart.itemAnimator = null
    }

    companion object {
        private const val DELETE_ITEM_MESSAGE = "장바구니에서 상품을 삭제했습니다!"
    }
}
