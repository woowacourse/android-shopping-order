package woowacourse.shopping.view.cart.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        initializeBindingVariables()
        observeState()
    }

    private fun initializeBindingVariables() {
        binding.viewModel = viewModel
    }

    private fun observeState() {
        viewModel.cartListUiState.observe(viewLifecycleOwner) { state ->
            showData(state.cartViewItems)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showData(data: List<ShoppingCartViewItem.CartViewItem>) {
        adapter.loadData(data)
    }

    private fun setUpAdapter() {
        adapter = CartAdapter(viewModel, viewModel)
        binding.rvCart.adapter = adapter
        binding.rvCart.itemAnimator = null
    }
}
