package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCouponBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory

class PaymentActivity :
    BindingActivity<ActivityCouponBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_coupon


    // Todo : curation에서 상품을 추가하면 orderItemsId에 추가되는데, 이를 어떻게 처리할지 고민해보기
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
                }

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




