package woowacourse.shopping.presentation.view.cart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartBinding
import woowacourse.shopping.presentation.view.cart.cartitem.CartItemFragment
import woowacourse.shopping.presentation.view.cart.recommendation.RecommendationFragment
import woowacourse.shopping.presentation.view.checkout.CheckoutFragment
import woowacourse.shopping.presentation.view.common.BaseFragment

class CartFragment : BaseFragment<FragmentCartBinding>(R.layout.fragment_cart) {
    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }

    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        }

    private val cartEventHandler =
        object : CartEventHandler {
            override fun onPlaceOrder() {
                if (viewModel.canPlaceOrder.value != true) {
                    Toast.makeText(activity, R.string.message_no_item_selected, Toast.LENGTH_LONG).show()
                    return
                }

                if (viewModel.canSelectItems.value == true) {
                    childFragmentManager.commit {
                        replace(R.id.cart_fragment_container, RecommendationFragment())
                    }
                    viewModel.disableSelection()
                    viewModel.fetchRecommendedProducts()
                } else {
                    parentFragmentManager.commit {
                        setReorderingAllowed(true)
                        val bundle = CheckoutFragment.newBundle(viewModel.selectedProductIds.value.orEmpty().toLongArray())
                        replace(R.id.shopping_fragment_container, CheckoutFragment::class.java, bundle)
                        addToBackStack(null)
                    }
                }
            }

            override fun onBatchSelect(isChecked: Boolean) {
                viewModel.selectAllCartItems(isChecked)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.cart_fragment_container, CartItemFragment::class.java, null)
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
            eventHandler = cartEventHandler
        }
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
    }

    private fun initObserver() {
        viewModel.allSelected.observe(viewLifecycleOwner) {
            binding.selectAll.isChecked = it
        }

        viewModel.canSelectItems.observe(viewLifecycleOwner) {
            binding.canSelectItems = it
        }

        viewModel.canPlaceOrder.observe(viewLifecycleOwner) {}
    }

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            remove(this@CartFragment)
        }
    }
}
