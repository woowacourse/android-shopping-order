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
    ItemCounterListener {
    private val viewModel: CartViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { CartViewModel.Factory },
    )

    private val recommendationAdapter: RecommendationAdapter by lazy {
        RecommendationAdapter(itemCounterListener = this)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initRecommendationAdapter()
    }

    private fun initRecommendationAdapter() {
        binding.recyclerViewRecommendationProduct.adapter = recommendationAdapter
        viewModel.recommendedProducts.observe(viewLifecycleOwner) {
            recommendationAdapter.updateRecommendedProducts(it)
        }
    }

    override fun increase(product: ProductUiModel) {
        TODO("Not yet implemented")
    }

    override fun decrease(product: ProductUiModel) {
        TODO("Not yet implemented")
    }
}
