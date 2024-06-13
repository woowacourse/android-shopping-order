package woowacourse.shopping.view.coupon

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.real.CouponRepositoryImpl
import woowacourse.shopping.data.repository.real.OrderRepositoryImpl
import woowacourse.shopping.databinding.FragmentCouponBinding
import woowacourse.shopping.utils.ShoppingUtils.makeToast
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.coupon.adapter.CouponItemRecyclerViewAdapter
import woowacourse.shopping.view.recommend.CouponEvent
import woowacourse.shopping.view.recommend.RecommendFragment

@Suppress("DEPRECATION")
class CouponFragment : Fragment(), OnclickNavigateCoupon {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentCouponBinding? = null
    private val binding get() = _binding!!
    private val couponViewModel: CouponViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory {
                CouponViewModel(
                    orderRepository = OrderRepositoryImpl(),
                    couponRepository = CouponRepositoryImpl(),
                )
            },
        )[CouponViewModel::class.java]
    }

    private lateinit var couponAdapter: CouponItemRecyclerViewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        couponViewModel.couponCalculator.coupons.observe(viewLifecycleOwner) {
            couponAdapter.submitList(it)
        }
        couponViewModel.couponEvent.observe(viewLifecycleOwner) { couponEvent ->
            when (couponEvent) {
                CouponEvent.OrderRecommends.Success -> navigateToProduct()
                CouponEvent.SelectCoupon.InvalidCount,
                CouponEvent.SelectCoupon.InvalidPrice,
                ->
                    requireContext().makeToast(
                        getString(R.string.invalid_coupon),
                    )

                CouponEvent.SelectCoupon.InvalidDate ->
                    requireContext().makeToast(
                        getString(R.string.invalid_coupon_date),
                    )

                CouponEvent.SelectCoupon.Success -> couponAdapter.notifyDataSetChanged()
            }
            couponAdapter.notifyDataSetChanged()
        }
        couponViewModel.errorEvent.observe(viewLifecycleOwner) {
            requireContext().makeToast(
                "Error",
            )
        }
    }

    private fun navigateToProduct() {
        requireContext().makeToast(
            getString(R.string.success_order),
        )
        mainActivityListener?.resetFragment()
    }

    private fun initView() {
        binding.vm = couponViewModel
        binding.onclickNavigatePayment = this
        binding.onClickPayment = couponViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        couponAdapter = CouponItemRecyclerViewAdapter(couponViewModel)
        binding.rvCoupon.adapter = couponAdapter
        loadCheckedShoppingCart()
        couponViewModel.loadCoupons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun clickBack() {
        mainActivityListener?.popFragment()
    }

    private fun loadCheckedShoppingCart() {
        try {
            val shoppingCart = receiveCheckedShoppingCart()
            if (shoppingCart != null) {
                couponViewModel.saveCheckedShoppingCarts(shoppingCart)
            }
        } catch (e: Exception) {
            requireContext().makeToast(
                getString(R.string.error_data_load),
            )
            clickBack()
        }
    }

    private fun receiveCheckedShoppingCart(): ShoppingCart? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(
                RecommendFragment.CHECKED_SHOPPING_CART,
                ShoppingCart::class.java,
            )
        } else {
            arguments?.getSerializable(RecommendFragment.CHECKED_SHOPPING_CART) as? ShoppingCart
        }
    }

    companion object {
        fun createBundle(checkedShoppingCart: ShoppingCart): Bundle {
            return Bundle().apply {
                putSerializable(CHECKED_SHOPPING_CART, checkedShoppingCart)
            }
        }

        private const val CHECKED_SHOPPING_CART = "checkedShoppingCart"
    }
}
