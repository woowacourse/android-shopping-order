package woowacourse.shopping.view.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartListBinding
import woowacourse.shopping.view.cart.adapter.CartAdapter
import woowacourse.shopping.view.cart.vm.CartViewModel
import woowacourse.shopping.view.cart.vm.CartViewModelFactory
import woowacourse.shopping.view.core.handler.CartQuantityHandler

class CartListFragment :
    Fragment(
        R.layout.fragment_cart_list,
    ),
    CartQuantityHandler,
    CartAdapter.Handler {
    private val viewModel: CartViewModel by activityViewModels {
        val container = (requireActivity().application as App).container
        CartViewModelFactory(
            container.cartRepository,
            container.productRepository,
        )
    }

    private val cartAdapter by lazy {
        CartAdapter(handler = this, cartQuantityHandler = this)
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

    override fun onClickDeleteItem(cardId: Long) {
        viewModel.deleteProduct(cardId)
    }

    override fun onCheckedChanged(
        cartId: Long,
        isChecked: Boolean,
    ) {
        viewModel.updateCheckedState(cartId, isChecked)
    }

    override fun onClickIncrease(productId: Long) {
        viewModel.increaseCartQuantity(productId)
    }

    override fun onClickDecrease(productId: Long) {
        viewModel.decreaseCartQuantity(productId)
    }
}
