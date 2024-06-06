package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.view.MenuItem
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.base.BaseActivity
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.model.CartsWrapper
import woowacourse.shopping.presentation.ui.payment.adapter.CouponsAdapter
import woowacourse.shopping.presentation.ui.productlist.ProductListActivity

class PaymentActivity : BaseActivity<ActivityPaymentBinding>(R.layout.activity_payment) {
    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModel.factory(
            CouponRepository.getInstance(),
            OrderRepository.getInstance(),
        )
    }

    private val couponsAdapter: CouponsAdapter by lazy { CouponsAdapter(viewModel) }

    override fun initCreateView() {
        initActionBar()
        initDataBinding()
        initAdapter()
        initObserve()
    }

    private fun initActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.payment_title)
        }
    }

    private fun initDataBinding() {
        binding.vm = viewModel
    }

    private fun initAdapter() {
        binding.rvCoupon.adapter = couponsAdapter
    }

    private fun initObserve() {
        viewModel.navigateAction.observeEvent(this) { navigateAction ->
            when (navigateAction) {
                is PaymentNavigateAction.NavigateToProductList -> {
                    val intent =
                        ProductListActivity.getIntent(this).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    startActivity(intent)
                }
            }
        }

        viewModel.message.observeEvent(this) { message ->
            when (message) {
                is PaymentMessage.PaymentSuccessMessage -> showToastMessage(message.getMessage(this))
            }
        }

        viewModel.uiState.observe(this) { state ->
            couponsAdapter.submitList(state.couponsState)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    companion object {
        const val PUT_EXTRA_CARTS_WRAPPER_KEY = "PUT_EXTRA_CARTS_WRAPPER_KEY"

        fun getIntent(
            context: Context,
            cartsWrapper: CartsWrapper,
        ): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putExtra(PUT_EXTRA_CARTS_WRAPPER_KEY, cartsWrapper)
            }
        }
    }
}
