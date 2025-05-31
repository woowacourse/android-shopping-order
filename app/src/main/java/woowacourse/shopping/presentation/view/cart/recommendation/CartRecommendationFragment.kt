package woowacourse.shopping.presentation.view.cart.recommendation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommendationBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.CartViewModel
import woowacourse.shopping.presentation.view.cart.recommendation.adapter.RecommendationAdapter

class CartRecommendationFragment :
    BaseFragment<FragmentCartRecommendationBinding>(R.layout.fragment_cart_recommendation),
    RecommendEventListener,
    ItemCounterListener {
    private val viewModel: CartViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { CartViewModel.Factory },
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

        initRecommendationAdapter()
        initObserver()
    }

    private fun initObserver() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            recommendationAdapter.updateRecommendedProducts(products)
        }
        viewModel.itemUpdateEvent.observe(viewLifecycleOwner) { product ->
            recommendationAdapter.updateItem(product)
        }
    }

    private fun initRecommendationAdapter() {
        binding.recyclerViewRecommendationProduct.adapter = recommendationAdapter
        viewModel.recommendedProducts.observe(viewLifecycleOwner) {
            recommendationAdapter.updateRecommendedProducts(it)
        }
    }

    override fun increase(product: ProductUiModel) {
        viewModel.increaseAmount(product)
    }

    override fun decrease(product: ProductUiModel) {
        viewModel.decreaseAmount(product)
    }

    override fun onInitialAddToCart(product: ProductUiModel) {
        viewModel.addToCart(product)
    }
}

interface RecommendEventListener {
    fun onInitialAddToCart(product: ProductUiModel)
}
