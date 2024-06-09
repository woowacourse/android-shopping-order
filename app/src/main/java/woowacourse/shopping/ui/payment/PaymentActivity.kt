package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteOrderDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remotePaymentDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.ui.home.HomeActivity
import woowacourse.shopping.ui.payment.action.PaymentNavigationActions.NavigateToBack
import woowacourse.shopping.ui.payment.action.PaymentNavigationActions.NavigateToHome
import woowacourse.shopping.ui.payment.action.PaymentNotifyingActions.NotifyPaymentCompleted
import woowacourse.shopping.ui.payment.adapter.CouponAdapter
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModelFactory
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewmodel
import woowacourse.shopping.ui.state.UiState

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: CouponAdapter
    private val cartItemIds: ArrayList<Int> by lazy {
        intent.getIntegerArrayListExtra(CART_ITEM_IDS) ?: arrayListOf()
    }
    private val viewModel: PaymentViewmodel by viewModels {
        PaymentViewModelFactory(
            cartItemIds,
            CartRepositoryImpl(remoteCartDataSource),
            OrderRepositoryImpl(
                remoteOrderDataSource,
            ),
            CouponRepositoryImpl(remotePaymentDataSource),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpDataBinding()
        setUpAdapter()
        observeViewModel()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setUpAdapter() {
        adapter = CouponAdapter(viewModel)
        binding.rvCoupon.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.paymentUiState.observe(this) { state ->
            when (state) {
                is UiState.Success -> showData(state.data)
                is UiState.Loading -> showData(emptyList())
                is UiState.Error ->
                    showError(
                        state.exception.message
                            ?: getString(R.string.unknown_error),
                    )
            }
        }

        viewModel.paymentNavigationActions.observe(this) { paymentNavigationActions ->
            paymentNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is NavigateToBack -> {
                        finish()
                    }

                    is NavigateToHome -> {
                        finish()
                        navigateToHome()
                    }
                }
            }
        }

        viewModel.paymentNotifyingActions.observe(this) { paymentNotifyingActions ->
            paymentNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is NotifyPaymentCompleted -> {
                        notifyPaymentCompleted()
                    }
                }
            }
        }
    }

    private fun showData(data: List<CouponState>) {
        adapter.submitList(data.toList())
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        startActivity(HomeActivity.createIntent(this))
    }

    private fun notifyPaymentCompleted() {
        Toast.makeText(this, PAYMENT_COMPLETED_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CART_ITEM_IDS = "cartItemIds"
        private const val PAYMENT_COMPLETED_MESSAGE = "상품 주문이 완료되었습니다!"

        fun createIntent(
            context: Context,
            cartItemIds: List<Int>,
        ): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putExtra(CART_ITEM_IDS, ArrayList(cartItemIds))
            }
        }
    }
}
