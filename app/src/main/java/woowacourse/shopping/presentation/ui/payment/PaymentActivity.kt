package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCouponBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.shopping.ShoppingActionActivity

class PaymentActivity :
    BindingActivity<ActivityCouponBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_coupon


    private val orderItemsId: List<Long> by lazy {
        intent.getIntegerArrayListExtra(EXTRA_ORDER_PRODUCT)?.map { it.toLong() } ?: emptyList()
    }

    private val viewModel: PaymentViewModel by viewModels { ViewModelFactory(orderItemsId) }
    private val couponAdapter: CouponAdapter by lazy { CouponAdapter(viewModel) }

    override fun initStartView() {
        binding.rvCoupon.adapter = couponAdapter
        binding.couponActionHandler = viewModel

        initData()
        initObserver()
    }

    private fun initData() {
        viewModel.getCoupons()
    }

    private fun initObserver() {
        viewModel.coupons.observe(this) {
            when (it) {
                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    couponAdapter.submitList(it.data)
                    couponAdapter.notifyDataSetChanged()
                }

            }
        }

        viewModel.orderProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    binding.tvPaymentPrice.text =
                        getString(
                            R.string.won,
                            it.data.sumOf {
                                it.quantity * it.price
                            },
                        )
                }
            }
        }
        viewModel.couponPrice.observe(this) {
            binding.tvPaymentCouponPrice.text = getString(R.string.discount_won, it)
        }
        viewModel.deliveryPrice.observe(this) {
            binding.tvPaymentDeliveryPrice.text = getString(R.string.won, it)
        }
        viewModel.totalPrice.observe(this) {
            binding.tvPaymentTotalPrice.text = getString(R.string.won, it)
        }

        viewModel.eventHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is CouponEvent.SuccessPay -> {
                        Toast.makeText(this, "주문이 성공적으로 진행되었습니다.", Toast.LENGTH_SHORT).show()
                        ShoppingActionActivity.createIntent(this).run {
                            startActivity(this)
                            finish()
                        }
                    }
                }
            }
        )

        viewModel.errorHandler.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    companion object {
        const val EXTRA_ORDER_PRODUCT = "orderProduct"
        fun createIntent(
            context: Context,
            orderItemsId: List<Int>,
        ): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putIntegerArrayListExtra(
                    EXTRA_ORDER_PRODUCT,
                    orderItemsId as ArrayList<Int>
                )
            }
        }
    }
}




