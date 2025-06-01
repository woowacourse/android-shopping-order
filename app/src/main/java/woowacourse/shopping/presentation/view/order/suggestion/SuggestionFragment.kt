package woowacourse.shopping.presentation.view.order.suggestion

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentSuggestionBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.order.OrderNavigator
import woowacourse.shopping.presentation.view.order.OrderViewModel
import woowacourse.shopping.presentation.view.order.suggestion.adapter.SuggestionAdapter
import woowacourse.shopping.presentation.view.order.suggestion.event.SuggestionMessageEvent

class SuggestionFragment :
    BaseFragment<FragmentSuggestionBinding>(R.layout.fragment_suggestion),
    SuggestionAdapter.SuggestionEventListener {
    private val sharedViewModel: OrderViewModel by activityViewModels()
    private val viewModel: SuggestionViewModel by viewModels { SuggestionViewModel.Factory }
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
    }

    override fun onQuantitySelectorOpenButtonClick(productId: Long) {
        sharedViewModel.increaseProductQuantity(productId)
    }

    override fun increaseQuantity(productId: Long) {
        sharedViewModel.increaseProductQuantity(productId)
    }

    override fun decreaseQuantity(productId: Long) {
        sharedViewModel.decreaseProductQuantity(productId)
    }

    private fun setAdapter() {
        binding.recyclerViewSuggestion.adapter = suggestionAdapter
    }

    private fun setObservers() {
        viewModel.suggestionProducts.observe(viewLifecycleOwner) { suggestionProducts ->
            suggestionAdapter.submitList(suggestionProducts)
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) { event ->
            showToast(event.toMessageResId())
        }

        sharedViewModel.cartProducts.observe(viewLifecycleOwner) {
            viewModel.fetchSuggestionProducts()
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

    private fun SuggestionMessageEvent.toMessageResId(): Int =
        when (this) {
            SuggestionMessageEvent.FIND_PRODUCT_QUANTITY_FAILURE ->
                R.string.suggestion_screen_event_message_find_quantity_failure

            SuggestionMessageEvent.FETCH_SUGGESTION_PRODUCT_FAILURE ->
                R.string.suggestion_screen_event_message_fetch_suggestion_product_failure
        }
}
