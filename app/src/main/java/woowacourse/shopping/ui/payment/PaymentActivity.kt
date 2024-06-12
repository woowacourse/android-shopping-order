package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.remote.service.NetworkModule
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.payment.adapter.CouponAdapter
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModel
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModelFactory
import woowacourse.shopping.ui.products.ProductContentsActivity

class PaymentActivity : AppCompatActivity() {
    private var toast: Toast? = null
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
        observePaying()
        initToolbarBinding()
        observeError()
    }

    private fun initToolbarBinding() {
        binding.toolbarPayment.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setCouponAdapter() {
        binding.rvCoupon.itemAnimator = null
        adapter = CouponAdapter(viewModel)
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

    private fun observePaying() {
        viewModel.paying.observe(this) {
            toast?.cancel()
            toast =
                Toast.makeText(
                    this,
                    getString(R.string.paying_complete_comment),
                    Toast.LENGTH_SHORT,
                )
            toast?.show()
            ProductContentsActivity.startActivity(this)
        }
    }

    private fun observeError() {
        viewModel.error.observe(this) {
            toast = Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
            toast?.show()
        }
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
