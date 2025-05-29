package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentSuggestionBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.cart.adapter.SuggestionAdapter

class SuggestionFragment :
    BaseFragment<FragmentSuggestionBinding>(R.layout.fragment_suggestion),
    SuggestionAdapter.SuggestionEventListener {
    private val viewModel: OrderViewModel by activityViewModels()
    private val suggestionAdapter by lazy { SuggestionAdapter(this) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        setBackPressedDispatcher()
        setAdapter()
        setObservers()

        if (savedInstanceState == null) {
            viewModel.fetchSuggestionProducts()
        }
    }

    override fun onQuantitySelectorOpenButtonClick(productId: Long) {
        viewModel.increaseProductQuantity(productId, RefreshTarget.SUGGESTION)
    }

    override fun increaseQuantity(productId: Long) {
        viewModel.increaseProductQuantity(productId, RefreshTarget.SUGGESTION)
    }

    override fun decreaseQuantity(productId: Long) {
        viewModel.decreaseProductQuantity(productId, RefreshTarget.SUGGESTION)
    }

    private fun setAdapter() {
        binding.recyclerViewSuggestion.adapter = suggestionAdapter
    }

    private fun setObservers() {
        viewModel.suggestionProducts.observe(viewLifecycleOwner) { suggestionProducts ->
            suggestionAdapter.submitList(suggestionProducts)
        }
    }

    private fun setupActionBar() {
        binding.toolbarCart.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarCart.setNavigationOnClickListener {
            navigateToCart()
        }
    }

    private fun setBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToCart()
        }
    }

    private fun navigateToCart() {
        (requireActivity() as? OrderNavigator)?.navigateToCart()
    }
}
