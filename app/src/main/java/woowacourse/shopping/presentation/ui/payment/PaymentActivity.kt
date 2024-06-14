package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.presentation.state.UIState

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var couponAdapter: CouponAdapter
    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(OrderRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.eventHandler = viewModel
        observeViewModel()
    }

    private fun setUpRecyclerView() {
        couponAdapter = CouponAdapter(viewModel)
        binding.recyclerviewAvailableCouponList.adapter = couponAdapter
    }

    private fun observeViewModel() {
        viewModel.availableCouponsUiState.observe(this) { uiState ->
            when (uiState) {
                is UIState.Success -> couponAdapter.submitCoupons(uiState.data)
                is UIState.Empty -> {}
                is UIState.Error -> {
                    showToastMessage(
                        uiState.exception.message ?: getString(R.string.unknown_error),
                    )
                }
            }
        }

        viewModel.changedCouponIds.observe(this) {
            couponAdapter.updateChecked(it)
        }

        viewModel.moveBack.observe(this) {
            if (it == true) {
                finish()
            }
        }

        viewModel.isOrderSuccess.observe(this) {
            if (it == true) {
                finish()
            }
        }

        viewModel.toastMessage.observe(this) {
            showToastMessage(it)
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCoupons()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, PaymentActivity::class.java)
        }
    }
}
