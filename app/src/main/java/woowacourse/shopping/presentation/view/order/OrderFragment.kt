package woowacourse.shopping.presentation.view.order

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.databinding.FragmentOrderBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.extension.getParcelableArrayListCompat
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.view.order.coupon.CouponAdapter

class OrderFragment :
    BaseFragment<FragmentOrderBinding>(R.layout.fragment_order),
    CouponAdapter.SelectCouponListener {
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModel.factory(
            orderRepository = RepositoryProvider.orderRepository,
            couponRepository = RepositoryProvider.couponRepository,
        )
    }
    private val couponAdapter: CouponAdapter by lazy {
        CouponAdapter(
            selectCouponListener = this,
        )
    }

    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToScreen()
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(backCallback)

        val selectedItems =
            arguments?.getParcelableArrayListCompat<CartItemUiModel>(ARG_SELECTED_CART_ITEM)
        selectedItems?.let { viewModel.setSelectedItems(it) }

        initOrderSummary()
        initObserver()
    }

    private fun initOrderSummary() {
        viewModel.calculateOrderSummary(coupon = null)
    }

    private fun initObserver() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.recyclerViewCoupon.adapter = couponAdapter

        viewModel.loadCoupons()

        viewModel.couponList.observe(viewLifecycleOwner) {
            couponAdapter.submitList(it)
        }

        viewModel.selectedCoupon.observe(viewLifecycleOwner) { selectedCoupon ->
            viewModel.calculateOrderSummary(selectedCoupon)
        }
    }

    private fun navigateToScreen() {
        parentFragmentManager.setFragmentResult("cart_update_result", Bundle())

        parentFragmentManager.popBackStack()
        parentFragmentManager.commit {
            remove(this@OrderFragment)
        }
    }

    override fun onSelectCoupon(coupon: CouponUiModel) {
        viewModel.selectCoupon(coupon)
    }

    companion object {
        private const val ARG_SELECTED_CART_ITEM = "selected_cart_item"

        fun newInstance(items: List<CartItemUiModel>): OrderFragment =
            OrderFragment().apply {
                arguments = bundleOf(ARG_SELECTED_CART_ITEM to ArrayList(items))
            }
    }
}
