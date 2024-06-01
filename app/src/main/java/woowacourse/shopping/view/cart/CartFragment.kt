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
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.cartUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> return@observe
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }
        viewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }
        viewModel.updatedCartItem.observe(viewLifecycleOwner) { cartItem ->
            adapter.updateCartItemQuantity(cartItem)
        }

        viewModel.notifyDeletion.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                alertDeletion()
            }
        }

        viewModel.selectChangeId.observe(viewLifecycleOwner) {
            adapter.updateSelection(it)
        }
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    private fun showData(data: List<ShoppingCartViewItem.CartViewItem>) {
        adapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
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
