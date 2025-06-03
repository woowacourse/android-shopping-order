package woowacourse.shopping.presentation.view.cart.cartItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.databinding.FragmentCartItemBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.CartViewModel
import woowacourse.shopping.presentation.view.cart.adapter.CartItemAdapter

class CartItemFragment :
    BaseFragment<FragmentCartItemBinding>(R.layout.fragment_cart_item),
    CartItemEventListener,
    ItemCounterListener {
    private val viewModel: CartViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = {
            CartViewModel.factory(
                productRepository = RepositoryProvider.productRepository,
                cartRepository = RepositoryProvider.cartRepository,
            )
        },
    )

    private val cartItemAdapter: CartItemAdapter by lazy {
        CartItemAdapter(
            eventListener = this,
            itemCounterListener = this,
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setCartAdapter()
        initObserver()
    }

    private fun setCartAdapter() {
        binding.recyclerViewCart.adapter = cartItemAdapter
        viewModel.page.observe(viewLifecycleOwner) {
            binding.recyclerViewCart.smoothScrollToPosition(0)
        }
    }

    private fun initObserver() {
        viewModel.cartItems.observe(viewLifecycleOwner) {
            cartItemAdapter.updateCartItems(it)
        }

        viewModel.deleteState.observe(viewLifecycleOwner) {
            it?.let {
                cartItemAdapter.removeProduct(it)
                viewModel.fetchShoppingCart(isNextPage = false, isRefresh = true)
            }
        }

        viewModel.itemUpdateEvent.observe(viewLifecycleOwner) {
            cartItemAdapter.updateItem(it.toCartItem().toCartItemUiModel())
        }
    }

    override fun onProductDeletion(cartItem: CartItemUiModel) {
        viewModel.deleteProduct(cartItem)
    }

    override fun onProductSelectionToggle(
        cartItem: CartItemUiModel,
        isChecked: Boolean,
    ) {
        viewModel.setCartItemSelection(cartItem, isChecked)
    }

    override fun increase(product: ProductUiModel) {
        viewModel.increaseAmount(product)
    }

    override fun decrease(product: ProductUiModel) {
        viewModel.decreaseAmount(product)
    }
}

interface CartItemEventListener {
    fun onProductDeletion(cartItem: CartItemUiModel)

    fun onProductSelectionToggle(
        cartItem: CartItemUiModel,
        isChecked: Boolean,
    )
}
