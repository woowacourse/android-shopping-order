package woowacourse.shopping.presentation.view.checkout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCheckoutBinding
import woowacourse.shopping.presentation.view.catalog.CatalogFragment
import woowacourse.shopping.presentation.view.checkout.adapter.CouponAdapter
import woowacourse.shopping.presentation.view.checkout.adapter.CouponUiModel
import woowacourse.shopping.presentation.view.common.BaseFragment

class CheckoutFragment : BaseFragment<FragmentCheckoutBinding>(R.layout.fragment_checkout) {
    private val selectedProductIds: List<Long> by lazy {
        arguments?.getLongArray(
            SELECTED_PRODUCT_IDS,
        )?.toList().orEmpty()
    }
    private val viewModel: CheckoutViewModel by viewModels {
        CheckoutViewModel.factory(
            selectedProductIds,
        )
    }
    private val adapter: CouponAdapter by lazy { CouponAdapter(checkoutEventHandler) }

    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        }

    private val checkoutEventHandler =
        object : CheckoutEventHandler {
            override fun onToggleSelection(
                coupon: CouponUiModel,
                isSelected: Boolean,
            ) {
                viewModel.setCouponSelection(coupon, isSelected)
            }

            override fun onFinalizeOrder() {
                viewModel.finalizeOrder()
                Toast.makeText(activity, "주문이 완료되었습니다.", Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
                parentFragmentManager.commit {
                    replace(R.id.shopping_fragment_container, CatalogFragment::class.java, null)
                }
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

        val selectedProductIds = arguments?.getLongArray(SELECTED_PRODUCT_IDS)
        viewModel.loadSelectedCartItems()
        viewModel.loadCoupons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backCallback.remove()
    }

    private fun initBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.eventHandler = checkoutEventHandler
        binding.recyclerViewCoupon.adapter = adapter
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
    }

    private fun initObserver() {
        viewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            adapter.submitList(coupons)
        }
    }

    private fun navigateBack() {
        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            remove(this@CheckoutFragment)
        }
    }

    companion object {
        private const val SELECTED_PRODUCT_IDS = "selected_product_ids"

        fun newBundle(ids: LongArray) = bundleOf(SELECTED_PRODUCT_IDS to ids)
    }
}
