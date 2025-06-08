package woowacourse.shopping.feature.cart.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentOrderBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.cart.order.adapter.CouponAdapter

class OrderFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by activityViewModels {
        (requireActivity() as CartActivity).sharedViewModelFactory
    }

    private val couponAdapter: CouponAdapter by lazy {
        CouponAdapter(viewModel).apply {
            showSkeleton()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateAppBarTitle(getString(R.string.order_fragment_appbar_name))
        viewModel.updateWholeCoupons()
        setupBinding()
        setupRecyclerView()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setupRecyclerView() {
        binding.rvCouponItems.adapter = couponAdapter
    }
}
