package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentSuggestionBinding
import woowacourse.shopping.presentation.base.BaseFragment

class SuggestionFragment : BaseFragment<FragmentSuggestionBinding>(R.layout.fragment_suggestion) {
    private val viewModel: OrderViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        setBackPressedDispatcher()
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
