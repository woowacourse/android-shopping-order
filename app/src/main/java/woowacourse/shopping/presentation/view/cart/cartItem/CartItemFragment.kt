package woowacourse.shopping.presentation.view.cart.cartItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartItemBinding
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.cart.CartViewModel
import woowacourse.shopping.presentation.view.cart.cartItem.adapter.CartItemAdapter
import woowacourse.shopping.presentation.view.common.BaseFragment
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class CartItemFragment : BaseFragment<FragmentCartItemBinding>(R.layout.fragment_cart_item) {
    private val viewModel: CartViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { CartViewModel.Factory },
    )

    private val cartItemEventHandler =
        object : CartItemEventHandler {
            override fun onProductDeletion(cartItem: CartItemUiModel) {
                viewModel.removeFromCart(cartItem)
            }

            override fun onProductSelectionToggle(
                cartItem: CartItemUiModel,
                isChecked: Boolean,
            ) {
                viewModel.setCartItemSelection(cartItem, isChecked)
            }
        }

    private val itemCounterEventHandler =
        object : ItemCounterEventHandler {
            override fun increaseQuantity(product: ProductUiModel) {
                viewModel.increaseQuantity(product)
            }

            override fun decreaseQuantity(product: ProductUiModel) {
                viewModel.decreaseQuantity(product)
            }
        }

    private val cartItemAdapter: CartItemAdapter by lazy {
        CartItemAdapter(cartItemEventHandler, itemCounterEventHandler)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initObserver()
    }

    private fun initBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            recyclerViewCart.adapter = cartItemAdapter
        }
    }

    private fun initObserver() {
        with(viewModel) {
            cartItems.observe(viewLifecycleOwner) {
                cartItemAdapter.submitList(it)
            }

            deleteEvent.observe(viewLifecycleOwner) {
                cartItemAdapter.removeProduct(it)
                fetchShoppingCart(isNextPage = false, isRefresh = true)
                fetchRecommendedProducts()
            }

            itemUpdateEvent.observe(viewLifecycleOwner) {
                cartItemAdapter.updateItem(it)
            }
        }
    }
}
