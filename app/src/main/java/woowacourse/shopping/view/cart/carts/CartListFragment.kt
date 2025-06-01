package woowacourse.shopping.view.cart.carts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartListBinding
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory

class CartListFragment : Fragment(R.layout.fragment_cart_list) {
    private val viewModel: CartViewModel by activityViewModels {
        val container = (requireActivity().application as App).container
        CartViewModelFactory(
            container.cartRepository,
            container.productRepository,
        )
    }

    private val cartAdapter by lazy {
        CartAdapter(
            handler = viewModel.cartEventHandler,
            cartQuantityHandler = viewModel.cartQuantityEventHandler,
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCartListBinding.bind(view)
        setUpBinding(binding)
        initView(binding)
        observeViewModel(binding)
    }

    private fun setUpBinding(binding: FragmentCartListBinding) {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            adapter = cartAdapter
            vm = viewModel
        }
    }

    private fun initView(binding: FragmentCartListBinding) {
        binding.recyclerViewCart.apply {
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel(binding: FragmentCartListBinding) {
        viewModel.cartUiState.observe(viewLifecycleOwner) { value ->
            cartAdapter.submitList(value.items, value.pageState.page)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { value ->
            if (!value) {
                binding.recyclerViewCart.visibility = View.VISIBLE
                binding.shimmerLayout.visibility = View.GONE
            }
        }
    }
}
