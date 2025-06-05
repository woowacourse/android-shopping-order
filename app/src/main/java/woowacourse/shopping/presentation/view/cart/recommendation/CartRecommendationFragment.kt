package woowacourse.shopping.presentation.view.cart.recommendation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommendationBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.cart.CartViewModel
import woowacourse.shopping.presentation.view.cart.recommendation.adapter.RecommendationAdapter
import woowacourse.shopping.presentation.view.common.BaseFragment
import woowacourse.shopping.presentation.view.common.ItemCounterEventHandler

class CartRecommendationFragment : BaseFragment<FragmentCartRecommendationBinding>(R.layout.fragment_cart_recommendation) {
    private val viewModel: CartViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { CartViewModel.Factory },
    )

    private val recommendEventHandler =
        object : RecommendEventHandler {
            override fun onInitialAddToCart(product: ProductUiModel) {
                viewModel.increaseQuantity(product)
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

    private val recommendationAdapter: RecommendationAdapter by lazy {
        RecommendationAdapter(recommendEventHandler, itemCounterEventHandler)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewRecommendationProduct.adapter = recommendationAdapter
        initObserver()
    }

    private fun initObserver() {
        with(viewModel) {
            recommendedProducts.observe(viewLifecycleOwner) { products ->
                recommendationAdapter.submitList(products)
            }
            itemUpdateEvent.observe(viewLifecycleOwner) { product ->
                recommendationAdapter.updateItem(product)
            }
        }
    }
}
