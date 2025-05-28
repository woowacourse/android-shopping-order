package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.view.cart.event.CartMessageEvent

class CartFragment :
    BaseFragment<FragmentCartBinding>(R.layout.fragment_cart),
    CartAdapter.CartEventListener {
    private val cartAdapter: CartAdapter by lazy { CartAdapter(eventListener = this) }

    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        setupObservers()
        setupCartAdapter()
    }

    override fun onDeleteProduct(cartId: Long) {
        viewModel.deleteCartItem(cartId)
    }

    override fun increaseQuantity(productId: Long) {
        viewModel.increaseProductQuantity(productId)
    }

    override fun decreaseQuantity(productId: Long) {
        viewModel.decreaseProductQuantity(productId)
    }

    private fun setupActionBar() {
        binding.toolbarCart.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarCart.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupObservers() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.updateItemsManually(cartItems)
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) { event ->
            showToast(event.toMessageResId())
        }
    }

    private fun setupCartAdapter() {
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun CartMessageEvent.toMessageResId(): Int =
        when (this) {
            CartMessageEvent.FETCH_CART_ITEMS_FAILURE ->
                R.string.cart_screen_event_message_fetch_cart_items_failure

            CartMessageEvent.DELETE_CART_ITEM_FAILURE ->
                R.string.cart_screen_event_message_delete_cart_item_failure

            CartMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE ->
                R.string.cart_screen_event_message_patch_cart_product_quantity_failure

            CartMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE ->
                R.string.cart_screen_event_message_find_quantity_failure
        }
}
