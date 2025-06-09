package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.ui.common.DataBindingActivity
import woowacourse.shopping.ui.payment.adapter.CouponAdapter
import woowacourse.shopping.ui.payment.adapter.CouponViewHolder

class PaymentActivity : DataBindingActivity<ActivityPaymentBinding>(R.layout.activity_payment) {
    private val viewModel: PaymentViewModel by viewModels { PaymentViewModel.Factory }
    private val adapter by lazy {
        CouponAdapter(
            object : CouponViewHolder.OnclickHandler {
                override fun onCouponClick(id: Long) {
                    viewModel.selectCoupon(id)
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSupportActionBar()
        binding.lifecycleOwner = this
        binding.couponContainer.adapter = adapter
        binding.viewModel = viewModel
        initProductsInfo()
        initObservers()
    }

    private fun initProductsInfo() {
        val selectedProducts: Products =
            intent.getSerializableExtra(KEY_SELECTED_PRODUCTS_ID) as Products
        viewModel.loadProductsInfo(selectedProducts)
    }

    private fun initSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.payment_title)
    }

    private fun initObservers() {
        viewModel.uiState.observe(this) { uiState ->
            adapter.submitList(uiState.coupons)
        }
    }

    companion object {
        private const val KEY_SELECTED_PRODUCTS_ID = "selectedProductIds"

        fun newIntent(
            context: Context,
            selectedProducts: Products,
        ): Intent =
            Intent(context, PaymentActivity::class.java).apply {
                putExtra(KEY_SELECTED_PRODUCTS_ID, selectedProducts)
            }
    }
}
