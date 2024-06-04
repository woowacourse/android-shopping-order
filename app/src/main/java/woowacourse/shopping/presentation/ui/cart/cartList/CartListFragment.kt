package woowacourse.shopping.presentation.ui.cart.cartList

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
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        initCartAdapter()
        observeCartUpdates()
    }

    override fun onStart() {
        super.onStart()
        viewModel.setOrderState(OrderState.CartList)
    }

    private fun initCartAdapter() {
        binding.rvCarts.adapter = cartAdapter
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
