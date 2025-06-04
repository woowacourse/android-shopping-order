package woowacourse.shopping.presentation.view.cart.recommendation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.databinding.FragmentCartRecommendationBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.CartViewModel
import woowacourse.shopping.presentation.view.cart.recommendation.adapter.RecommendEventListener
import woowacourse.shopping.presentation.view.cart.recommendation.adapter.RecommendationAdapter

class CartRecommendationFragment :
    BaseFragment<FragmentCartRecommendationBinding>(R.layout.fragment_cart_recommendation),
    RecommendEventListener,
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

    private val recommendationAdapter: RecommendationAdapter by lazy {
        RecommendationAdapter(
            itemCounterListener = this,
            recommendEventListener = this,
        )
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

    override fun increase(product: ProductUiModel) {
        viewModel.increaseAmount(product)
    }

    override fun decrease(product: ProductUiModel) {
        viewModel.decreaseAmount(product)
    }

    override fun onInitialAddToCart(product: ProductUiModel) {
        viewModel.initialAddToCart(product)
    }

    private fun initObserver() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            recommendationAdapter.submitList(products ?: emptyList())
        }
        viewModel.itemUpdateEvent.observe(viewLifecycleOwner) { updatedProduct ->
            recommendationAdapter.updateItem(updatedProduct)
        }
    }
}
