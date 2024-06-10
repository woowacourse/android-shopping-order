package woowacourse.shopping.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.ui.FragmentNavigator

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

        viewModel.loadedCoupons.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.payEvent.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            (requireActivity() as FragmentNavigator).navigateToProductList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PaymentFragment"
    }
}
