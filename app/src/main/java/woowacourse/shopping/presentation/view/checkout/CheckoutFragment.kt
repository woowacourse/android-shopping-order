package woowacourse.shopping.presentation.view.checkout

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCheckoutBinding
import woowacourse.shopping.presentation.view.checkout.adapter.CouponAdapter
import woowacourse.shopping.presentation.view.common.BaseFragment

class CheckoutFragment : BaseFragment<FragmentCheckoutBinding>(R.layout.fragment_checkout) {
    private val viewModel: CheckoutViewModel by viewModels { CheckoutViewModel.Factory }
    private val adapter = CouponAdapter()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val selectedProductIds = arguments?.getLongArray(SELECTED_PRODUCT_IDS)

        initBinding()
        initObserver()
        viewModel.loadCoupons()
        viewModel.loadSelectedCartItems(selectedProductIds?.toList().orEmpty())
    }

    private fun initBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewCoupon.adapter = adapter
    }

    private fun initObserver() {
        viewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            adapter.submitList(coupons)
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartItems.forEach { Log.d("cartItems", "$it") }
        }
    }

    companion object {
        private const val SELECTED_PRODUCT_IDS = "selected_product_ids"

        fun newBundle(ids: LongArray) = bundleOf(SELECTED_PRODUCT_IDS to ids)
    }
}
