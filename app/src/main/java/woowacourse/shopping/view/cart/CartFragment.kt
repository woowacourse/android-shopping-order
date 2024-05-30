package woowacourse.shopping.view.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.data.db.cart.CartRepositoryImpl2
import woowacourse.shopping.data.model.CartItem2
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.domain.repository.CartRepository2
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.adapter.CartAdapter
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem
import woowacourse.shopping.view.detail.DetailActivity
import woowacourse.shopping.view.state.UIState

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = _binding!!

    private val viewModel by activityViewModels<CartViewModel>()
//    private val viewModel by viewModels<CartListViewModel> {
//        CartListViewModelFactory(CartRepositoryImpl2(remoteCartDataSource))
//    }

    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        binding.viewModel = viewModel

        viewModel.cartUiState.observe(viewLifecycleOwner) { state ->
            println("state : $state")
            when (state) {
                is UIState.Success -> showData(state.data)
                is UIState.Loading -> return@observe
                is UIState.Error ->
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
            println("cartItem : $cartItem")
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(DetailActivity.createIntent(requireContext(), productId))
    }

    private fun showData(data: List<ShoppingCartViewItem.CartViewItem>) {
        println("data : $data")
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
