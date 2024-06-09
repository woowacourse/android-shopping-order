package woowacourse.shopping.presentation.payment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.data.payment.CouponRepositoryInjector
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.cart.recommend.RecommendNavArgs

class PaymentFragment : BindingFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private lateinit var adapter: CouponAdapter
    private val viewModel by viewModels<PaymentViewModel> {
        val couponRepository = CouponRepositoryInjector.couponRepository()
        PaymentViewModel.factory(couponRepository)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            vm = viewModel
            Log.d("alsong", "onViewCreated: ${viewModel.coupons.value}")
            lifecycleOwner = viewLifecycleOwner
        }
        viewModel.coupons.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
        initViews()
    }

    private fun initViews() {
        adapter = CouponAdapter(viewModel, viewLifecycleOwner)
        binding?.rvCouponList?.adapter = adapter
    }

    companion object {
        val TAG: String? = PaymentFragment::class.java.canonicalName

        private const val ORDERED_PRODUCTS_KEY = "ORDERED_PRODUCTS_KEY"

        fun args(navArgs: RecommendNavArgs): Bundle =
            Bundle().apply {
                putParcelable(ORDERED_PRODUCTS_KEY, navArgs)
            }
    }
}
