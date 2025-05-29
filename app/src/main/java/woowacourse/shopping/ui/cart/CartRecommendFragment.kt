package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommandBinding
import woowacourse.shopping.ui.cart.adapter.CartRecommendAdapter
import woowacourse.shopping.ui.cart.adapter.CartRecommendViewHolder
import woowacourse.shopping.ui.common.DataBindingFragment

class CartRecommendFragment : DataBindingFragment<FragmentCartRecommandBinding>(R.layout.fragment_cart_recommand) {
    private val viewModel: CartViewModel by activityViewModels<CartViewModel>()
    private val adapter: CartRecommendAdapter by lazy { CartRecommendAdapter(createAdapterOnClickHandler()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadRecommendedProducts()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.cartItemsRecommendContainer.adapter = adapter
        viewModel.recommendedProducts.observe(requireActivity()) { recommendedProducts ->
            adapter.submitList(recommendedProducts.products)
            viewModel.updateOrderInfo()
        }
    }

    private fun createAdapterOnClickHandler() =
        object : CartRecommendViewHolder.OnClickHandler {
            override fun onIncreaseClick(productId: Long) {
                viewModel.increaseRecommendedProductQuantity(productId)
            }

            override fun onDecreaseClick(productId: Long) {
                viewModel.decreaseRecommendedProductQuantity(productId)
            }
        }
}
