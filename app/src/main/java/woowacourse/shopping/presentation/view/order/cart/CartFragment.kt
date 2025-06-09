package woowacourse.shopping.presentation.view.order.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.order.OrderUiEventListener
import woowacourse.shopping.presentation.view.order.OrderViewModel
import woowacourse.shopping.presentation.view.order.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.view.order.suggestion.SuggestionFragment
import woowacourse.shopping.presentation.view.order.toMessageResId

class CartFragment :
    BaseFragment<FragmentCartBinding>(R.layout.fragment_cart),
    OrderUiEventListener {
    private val viewModel: OrderViewModel by activityViewModels()
    private val cartAdapter: CartAdapter by lazy { CartAdapter(eventListener = viewModel) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        setupObservers()
        setupCartAdapter()
        binding.viewOrder.checkboxSelectAll.setOnClickListener {
            viewModel.onSwitchAllOrder()
        }
    }

    private fun setupActionBar() {
        binding.toolbarCart.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarCart.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun setupObservers() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.uiEvent = this

        viewModel.cartDisplayItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.submitList(cartItems)
        }

        viewModel.toastOrderEvent.observe(viewLifecycleOwner) { event ->
            showToast(event.toMessageResId())
        }
    }

    private fun setupCartAdapter() {
        binding.recyclerViewCart.adapter = cartAdapter
    }

    override fun order() {
        navigateToSuggestion()
    }

    private fun navigateToSuggestion() {
        parentFragmentManager.commit {
            hide(this@CartFragment)
            add(R.id.cart_fragment_container, SuggestionFragment::class.java, null)
            addToBackStack(null)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            parentFragmentManager.commit {
                show(this@CartFragment)
            }
        }
    }
}
