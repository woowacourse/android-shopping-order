package woowacourse.shopping.ui.payment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.order.OrderFragment
import woowacourse.shopping.ui.payment.adapter.CouponAdapter
import java.io.Serializable

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: PaymentViewModel

    private val adapter: CouponAdapter by lazy { CouponAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCoupons()
        viewModel.loadInitialPaymentInformation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater)
        initBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        observeIsPaymentSuccess()
        observeCoupons()
    }

    private fun initViewModel() {
        fetchOrderInformation()
        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun fetchOrderInformation() {
        arguments?.let { bundle ->
            val orderInformation =
                bundle.bundleSerializable(
                    OrderFragment.ORDER_INFORMATION,
                    OrderInformation::class.java
                ) ?: throw NoSuchElementException()
            factory = PaymentViewModel.factory(orderInformation)
        }
    }

    private fun initToolbar() {
        binding.toolbarPayment.setNavigationOnClickListener {
            (requireContext() as FragmentNavigator).popBackStack()
        }
    }

    private fun observeIsPaymentSuccess() {
        viewModel.isPaymentSuccess.observe(viewLifecycleOwner) { isPaymentSuccess ->
            if (isPaymentSuccess) {
                makeToast(getString(R.string.payment_success))
                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    private fun observeCoupons() {
        binding.rvPaymentCoupon.adapter = adapter
        viewModel.couponsUiModel.observe(viewLifecycleOwner) { coupons ->
            adapter.updateCoupons(coupons)
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun <T : Serializable> Bundle.bundleSerializable(
        key: String,
        clazz: Class<T>,
    ): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, clazz)
        } else {
            getSerializable(key) as? T
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
