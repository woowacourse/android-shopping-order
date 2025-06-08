package woowacourse.shopping.presentation.view.checkout

import android.os.Bundle
import android.view.View
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

        initBinding()
        initObserver()
        viewModel.loadCoupons()
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewCoupon.adapter = adapter
    }

    private fun initObserver() {
        viewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            adapter.submitList(coupons)
        }
    }
}
