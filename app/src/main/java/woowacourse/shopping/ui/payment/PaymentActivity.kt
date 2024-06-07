package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.coupon.CouponRepositoryImpl
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.service.NetworkModule
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.payment.adapter.CouponAdapter
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModel
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModelFactory

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: CouponAdapter
    private val orderedCartItemIds by lazy {
        orderedCartItemIds()
    }
    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(
            orderedCartItemIds,
            CartRepositoryImpl(
                CartRemoteDataSourceImpl(NetworkModule.cartItemService),
                OrderRemoteDataSourceImpl(NetworkModule.orderService),
            ),
            CouponRepositoryImpl(CouponRemoteDataSourceImpl(NetworkModule.couponService)),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setCouponAdapter()
        observeCoupons()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setCouponAdapter() {
        binding.rvCoupon.itemAnimator = null
        adapter = CouponAdapter()
        binding.rvCoupon.adapter = adapter
    }

    private fun observeCoupons() {
        viewModel.coupons.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun orderedCartItemIds(): List<Long> {
        return intent.getLongArrayExtra(PaymentKey.ORDERED_CART_ITEM_IDS)?.toList() ?: emptyList()
    }

    companion object {
        fun startActivity(
            context: Context,
            orderedCartItemIds: List<Long>,
        ) = Intent(context, PaymentActivity::class.java).run {
            putExtra(PaymentKey.ORDERED_CART_ITEM_IDS, orderedCartItemIds.toLongArray())
            context.startActivity(this)
        }
    }
}
