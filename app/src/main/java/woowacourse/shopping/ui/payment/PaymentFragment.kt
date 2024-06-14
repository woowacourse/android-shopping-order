package woowacourse.shopping.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.payment.event.PaymentError

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private val viewModel: PaymentViewModel by viewModels { PaymentViewModel.factory() }
    private val adapter: CouponAdapter by lazy {
        CouponAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvPaymentCoupon.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadOrders()

        observeCoupons()
        observeEvent()
        observeError()
    }

    private fun observeCoupons() {
        viewModel.loadedCoupons.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun observeEvent() {
        viewModel.payEvent.observe(viewLifecycleOwner) {
            showToast(R.string.success_order)
            (requireActivity() as FragmentNavigator).navigateToProductList()
        }
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            when (error) {
                PaymentError.DiscountAmount -> showToast(R.string.error_discount_amount)
                PaymentError.LoadCoupons -> showToast(R.string.error_load_coupons)
                PaymentError.LoadOrders -> showToast(R.string.error_load_orders)
                PaymentError.Order -> showToast(R.string.error_order)
            }
        }
    }

    private fun showToast(
        @StringRes stringId: Int,
    ) {
        Toast.makeText(
            requireContext(),
            stringId,
            Toast.LENGTH_SHORT,
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PaymentFragment"
    }
}
