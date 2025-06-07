package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartProductBinding
import woowacourse.shopping.ui.cart.adapter.CartProductAdapter
import woowacourse.shopping.ui.cart.adapter.CartProductViewHolder
import woowacourse.shopping.ui.common.DataBindingFragment

class CartProductFragment : DataBindingFragment<FragmentCartProductBinding>(R.layout.fragment_cart_product) {
    private val viewModel: CartViewModel by activityViewModels<CartViewModel>()
    private val cartProductAdapter: CartProductAdapter =
        CartProductAdapter(createAdapterOnClickHandler())

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initViewBinding()
        initObservers()
    }

    private fun initViewBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.cartProductItemsContainer.adapter = cartProductAdapter
        binding.cartProductItemsContainer.itemAnimator = null
    }

    private fun initObservers() {
        viewModel.uiState.observe(requireActivity()) { uiState ->
            cartProductAdapter.submitItems(uiState.cartProducts.products)
        }
    }

    private fun createAdapterOnClickHandler() =
        object : CartProductViewHolder.OnClickHandler {
            override fun onRemoveCartProductClick(
                cartId: Long,
                productId: Long,
            ) {
                viewModel.removeCartProduct(cartId, productId)
            }

            override fun onIncreaseClick(productId: Long) {
                viewModel.increaseCartProductQuantity(productId)
            }

            override fun onDecreaseClick(productId: Long) {
                viewModel.decreaseCartProductQuantity(productId)
            }

            override fun onSelectClick(cartId: Long) {
                viewModel.toggleCartProductSelection(cartId)
            }
        }
}
