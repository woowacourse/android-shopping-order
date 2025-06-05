package woowacourse.shopping.presentation.view.order.suggestion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentSuggestionBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.ShoppingActivity
import woowacourse.shopping.presentation.view.order.OrderUiEventListener
import woowacourse.shopping.presentation.view.order.OrderViewModel
import woowacourse.shopping.presentation.view.order.suggestion.adatper.SuggestionAdapter
import woowacourse.shopping.presentation.view.order.suggestion.event.SuggestionMessageEvent

class SuggestionFragment :
    BaseFragment<FragmentSuggestionBinding>(R.layout.fragment_suggestion),
    OrderUiEventListener {
    private val suggestionViewModel: SuggestionViewModel by viewModels { SuggestionViewModel.Factory }
    private val orderViewModel: OrderViewModel by activityViewModels()

    private val suggestionAdapter by lazy { SuggestionAdapter(suggestionViewModel) }

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
            suggestionViewModel.fetchSuggestionProducts()
        }
    }

    private fun setAdapter() {
        binding.recyclerViewSuggestion.adapter = suggestionAdapter
    }

    private fun setObservers() {
        binding.lifecycleOwner = this
        binding.uiEvent = this
        binding.orderViewModel = orderViewModel
        binding.suggestionViewModel = suggestionViewModel
        suggestionViewModel.suggestionProducts.observe(viewLifecycleOwner) { suggestionProducts ->
            suggestionAdapter.submitList(suggestionProducts)
        }

        suggestionViewModel.toastSuggestionEvent.observe(viewLifecycleOwner) { event ->
            showToast(event.toMessageResId())
        }

        suggestionViewModel.purchaseProducts.observe(viewLifecycleOwner) {
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
        parentFragmentManager.commit {
            parentFragmentManager.popBackStack()
        }
    }

    private fun SuggestionMessageEvent.toMessageResId(): Int =
        when (this) {
            SuggestionMessageEvent.PATCH_CART_PRODUCT_QUANTITY_FAILURE ->
                R.string.cart_screen_event_message_patch_cart_product_quantity_failure

            SuggestionMessageEvent.ORDER_CART_ITEMS_FAILURE -> R.string.suggestion_screen_event_message_order_failure
        }

    override fun order() {
        orderViewModel.orderCartItems(suggestionViewModel.purchaseProducts.value.orEmpty())
        navigateToShopping()
    }

    private fun navigateToShopping() {
        val intent =
            ShoppingActivity.newIntent(requireActivity()).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        startActivity(intent)
        requireActivity().finish()
    }
}
