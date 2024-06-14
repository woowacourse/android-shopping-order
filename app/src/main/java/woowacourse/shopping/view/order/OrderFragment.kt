package woowacourse.shopping.view.order

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.remote.RemoteOrderRepositoryImpl
import woowacourse.shopping.databinding.FragmentOrderBinding
import woowacourse.shopping.utils.ShoppingUtils.makeToast
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.order.adapter.CouponAdapter
import woowacourse.shopping.view.order.state.CouponUiState
import woowacourse.shopping.view.order.state.OrderUiState

class OrderFragment : Fragment(), OnClickOrder {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentOrderBinding? = null
    val binding: FragmentOrderBinding get() = _binding!!
    private lateinit var adapter: CouponAdapter
    private val orderViewModel: OrderViewModel by lazy {
        val viewModelFactory =
            ViewModelFactory {
                OrderViewModel(
                    orderRepository = RemoteOrderRepositoryImpl(),
                )
            }
        viewModelFactory.create(OrderViewModel::class.java)
    }

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
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = orderViewModel
        binding.onClickOrder = this
        adapter = CouponAdapter(orderViewModel)
        binding.rvCoupon.adapter = adapter
        observeData()
        loadCheckedShoppingCart()
    }

    private fun observeData() {
        orderViewModel.coupons.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        orderViewModel.couponUiState.observe(viewLifecycleOwner) {
            when (it) {
                is CouponUiState.Applied -> {
                    requireContext().makeToast(getString(R.string.success_coupon_apply))
                }
                is CouponUiState.Error -> {
                    requireContext().makeToast(it.errorMessage)
                }
                is CouponUiState.Idle -> {}
            }
        }
        orderViewModel.orderUiState.observe(viewLifecycleOwner) {
            when (it) {
                is OrderUiState.Success -> {
                    requireContext().makeToast(getString(R.string.success_order))
                }
                is OrderUiState.Failure -> {
                    it.errorMessage?.let { error -> requireContext().makeToast(error) }
                }
                is OrderUiState.Idle -> {}
            }
        }
    }

    private fun receiveCheckedShoppingCart(): ShoppingCart {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(CHECKED_SHOPPING_CART, ShoppingCart::class.java)
                ?: throw NoSuchDataException()
        } else {
            arguments?.getSerializable(CHECKED_SHOPPING_CART) as? ShoppingCart
                ?: throw NoSuchDataException()
        }
    }

    private fun loadCheckedShoppingCart() {
        try {
            val shoppingCart = receiveCheckedShoppingCart()
            orderViewModel.saveCheckedShoppingCarts(shoppingCart)
        } catch (e: Exception) {
            requireContext().makeToast(getString(R.string.error_data_load))
            clickBack()
        }
    }

    override fun clickOrder() {
        orderViewModel.orderItems()
        mainActivityListener?.resetFragment()
    }

    override fun clickBack() {
        mainActivityListener?.popFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivityListener = null
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
