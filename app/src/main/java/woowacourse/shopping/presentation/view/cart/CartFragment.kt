package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.presentation.view.cart.cartItem.CartItemFragment
import woowacourse.shopping.presentation.view.cart.recommendation.CartRecommendationFragment
import woowacourse.shopping.presentation.view.common.BaseFragment

class CartFragment : BaseFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }

    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        }

    private val cartEventListener =
        object : CartEventListener {
            override fun onPlaceOrder() {
                childFragmentManager.commit {
                    replace(R.id.cart_fragment_container, CartRecommendationFragment())
                }
                binding.checkboxWrapper.isVisible = false
                viewModel.fetchRecommendedProducts()
            }

            override fun onBatchSelect(isChecked: Boolean) {
                viewModel.setAllSelections(isChecked)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.cart_fragment_container, CartItemFragment::class.java, null)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initObserver()
        requireActivity().onBackPressedDispatcher.addCallback(backCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback.remove()
    }

    private fun initBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
            eventListener = cartEventListener
        }
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
    }

    private fun initObserver() {
        viewModel.allSelected.observe(viewLifecycleOwner) {
            binding.selectAll.isChecked = it
        }
    }

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            remove(this@CartFragment)
        }
    }
}
