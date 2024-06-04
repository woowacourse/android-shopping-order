package woowacourse.shopping.presentation.ui.cart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.databinding.FragmentCartListBinding
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import woowacourse.shopping.presentation.ui.cart.OrderState
import woowacourse.shopping.presentation.ui.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.util.EventObserver

class CartListFragment : Fragment() {
    lateinit var binding: FragmentCartListBinding
    private val viewModel: CartViewModel by activityViewModels()
    private val cartAdapter: CartAdapter by lazy { CartAdapter(cartHandler = viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartListBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner
        initCartAdapter()
        observeErrorEventUpdates()
        observeCartUpdates()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setOrderState(OrderState.CartList)
        viewModel.loadAllCartItems(50)
    }

    private fun initCartAdapter() {
        binding.rvCarts.adapter = cartAdapter
    }

    private fun observeErrorEventUpdates() {
        viewModel.error.observe(
            viewLifecycleOwner,
            EventObserver {
            },
        )
    }

    private fun observeCartUpdates() {
        viewModel.cartItems.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    cartAdapter.updateList(it.data)
                }
            }
        }
    }
}
