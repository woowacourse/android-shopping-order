package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.cartItem.CartItemFragment
import woowacourse.shopping.presentation.view.cart.recommendation.CartRecommendationFragment

class CartFragment :
    BaseFragment<FragmentCartBinding>(R.layout.fragment_cart),
    CartEventListener,
    ItemCounterListener {
    private val viewModel: CartViewModel by viewModels {
        CartViewModel.factory(
            productRepository = RepositoryProvider.productRepository,
            cartRepository = RepositoryProvider.cartRepository,
        )
    }
    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateToScreen()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initObserver()
        initListener()

        requireActivity().onBackPressedDispatcher.addCallback(backCallback)

        binding.selectAll.setOnClickListener { view ->
            onBatchSelect(binding.selectAll.isChecked)
        }
        binding.btnPlaceOrder.setOnClickListener {
            navigateToRecommendation()
        }
    }

    private fun navigateToScreen() {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.cart_fragment_container, CartItemFragment::class.java, null)
        }
    }

    private fun navigateBack() {
        parentFragmentManager.setFragmentResult("cart_update_result", Bundle())

        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            remove(this@CartFragment)
        }
    }

    private fun initObserver() {
        viewModel.allSelected.observe(viewLifecycleOwner) {
            binding.selectAll.isChecked = it
        }
    }

    private fun navigateToRecommendation() {
        childFragmentManager.commit {
            replace(R.id.cart_fragment_container, CartRecommendationFragment())
        }
        binding.selectAll.isVisible = false
        binding.tvSelectAllDescription.isVisible = false
        viewModel.fetchRecommendedProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback.remove()
    }

    override fun onBatchSelect(isChecked: Boolean) {
        viewModel.setAllSelections(isChecked)
    }

    override fun increase(product: ProductUiModel) {
        viewModel.increaseAmount(product)
    }

    override fun decrease(product: ProductUiModel) {
        viewModel.decreaseAmount(product)
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
        binding.eventListener = this
    }
}
