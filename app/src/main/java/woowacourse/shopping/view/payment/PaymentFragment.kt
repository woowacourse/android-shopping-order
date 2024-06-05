package woowacourse.shopping.view.payment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.remote.RemoteCouponRepositoryImpl
import woowacourse.shopping.data.repository.remote.RemoteOrderRepositoryImpl
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.utils.ShoppingUtils.makeToast
import woowacourse.shopping.utils.exception.ErrorEvent
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.payment.adapter.CouponAdapter
import woowacourse.shopping.view.recommend.RecommendFragment

class PaymentFragment : Fragment(), OnclickNavigatePayment {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentPaymentBinding? = null
    val binding: FragmentPaymentBinding get() = _binding!!
    private val paymentViewModel: PaymentViewModel by lazy {
        val viewModelFactory =
            ViewModelFactory {
                PaymentViewModel(
                    orderRepository = RemoteOrderRepositoryImpl(),
                    couponRepository = RemoteCouponRepositoryImpl(),
                )
            }
        viewModelFactory.create(PaymentViewModel::class.java)
    }
    private lateinit var adapter: CouponAdapter

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
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
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

    private fun initView() {
        binding.vm = paymentViewModel
        binding.onclickNavigatePayment = this
        binding.onClickPayment = paymentViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        adapter =
            CouponAdapter(
                onclickPayment = paymentViewModel,
            )
        binding.rvCoupon.adapter = adapter
        loadCheckedShoppingCart()
        paymentViewModel.loadCoupons()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        paymentViewModel.couponCalculator.coupons.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        paymentViewModel.paymentEvent.observe(viewLifecycleOwner) { paymentEvent ->
            when (paymentEvent) {
                PaymentEvent.Order.Success -> navigateToProduct()
                PaymentEvent.SelectCoupon.InvalidCount,
                PaymentEvent.SelectCoupon.InvalidPrice,
                ->
                    requireContext().makeToast(
                        getString(R.string.invalid_coupon),
                    )
                PaymentEvent.SelectCoupon.InvalidDate ->
                    requireContext().makeToast(
                        getString(R.string.invalid_coupon_date),
                    )
                PaymentEvent.SelectCoupon.Success -> adapter.notifyDataSetChanged()
            }
            adapter.notifyDataSetChanged()
        }
        paymentViewModel.errorEvent.observe(viewLifecycleOwner) { errorState ->
            requireContext().makeToast(
                errorState.receiveErrorMessage(),
            )
        }
    }

    override fun clickBack() {
        mainActivityListener?.popFragment()
    }

    private fun loadCheckedShoppingCart() {
        try {
            val shoppingCart = receiveCheckedShoppingCart()
            paymentViewModel.saveCheckedShoppingCarts(shoppingCart)
        } catch (e: Exception) {
            requireContext().makeToast(
                getString(R.string.error_data_load),
            )
            clickBack()
        }
    }

    private fun receiveCheckedShoppingCart(): ShoppingCart {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(
                RecommendFragment.CHECKED_SHOPPING_CART,
                ShoppingCart::class.java,
            )
                ?: throw ErrorEvent.LoadDataEvent()
        } else {
            arguments?.getSerializable(RecommendFragment.CHECKED_SHOPPING_CART) as? ShoppingCart
                ?: throw ErrorEvent.LoadDataEvent()
        }
    }

    private fun navigateToProduct() {
        mainActivityListener?.resetFragment()
        requireContext().makeToast(
            getString(R.string.success_order),
        )
    }
}
