package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.detail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.payment.adapter.PaymentAdapter
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel
import woowacourse.shopping.utils.getParcelableExtraCompat

class PaymentActivity : BindingActivity<ActivityPaymentBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_payment

    private val viewModel: PaymentActionViewModel by viewModels { ViewModelFactory() }
    private val paymentAdapter: PaymentAdapter by lazy { PaymentAdapter(viewModel) }

    override fun initStartView() {
        initData()
        initTitle()
        initAdapter()
        initObserver()
    }

    private fun initData() {
        intent.getParcelableExtraCompat<PaymentUiModel>(EXTRA_PAYMENT_UI_MODEL)?.let {
            viewModel.setPaymentUiModel(it)
        } ?: run {
            Toast.makeText(this, "데이터가 없습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun initTitle() {
        title = getString(R.string.payment_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapter() {
        binding.rvCoupon.adapter = paymentAdapter
    }

    private fun initObserver() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.getTickets()
        viewModel.coupons.observe(this) {
            paymentAdapter.submitList(it.couponUiModels)
        }
        viewModel.errorHandler.observe(this, EventObserver {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        private const val EXTRA_PAYMENT_UI_MODEL = "paymentUiModel"
        fun createIntent(context: Context, paymentUiModel: PaymentUiModel): Intent {
            return Intent(context, PaymentActivity::class.java)
                .apply {
                    putExtra(EXTRA_PAYMENT_UI_MODEL, paymentUiModel)
                }
        }
    }
}