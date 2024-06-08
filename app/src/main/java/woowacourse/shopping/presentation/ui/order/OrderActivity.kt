package woowacourse.shopping.presentation.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import woowacourse.shopping.presentation.util.EventObserver

class OrderActivity : BindingActivity<ActivityOrderBinding>() {
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModel.Companion.Factory(
            getSelectedCartIds(),
            getTotalOrderPrice(),
        )
    }

    private val couponAdapter: CouponAdapter by lazy { CouponAdapter(orderHandler = viewModel) }
    override val layoutResourceId: Int
        get() = R.layout.activity_order

    override fun initStartView(savedInstanceState: Bundle?) {
        initActionBarTitle()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvCoupons.adapter = couponAdapter
        observeData()
    }

    private fun initActionBarTitle() {
        title = getString(R.string.order_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getSelectedCartIds(): List<Long> = intent.getLongArrayExtra(EXTRA_CART_SELECTED_ITEMS)?.toList() ?: emptyList()

    private fun getTotalOrderPrice(): Long = intent.getLongExtra(EXTRA_TOTAL_PRICE_WITHOUT_DISCOUNT, 0)

    private fun observeData() {
        viewModel.coupons.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    couponAdapter.submitList(state.data)
                }
                is UiState.Loading -> {}
            }
        }

        viewModel.completeOrder.observe(
            this,
            EventObserver {
                if (it) {
                    showToast(getString(R.string.order_complete_success_message))
                    finish()
                    startActivity(ShoppingActivity.createIntent(this))
                }
            },
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    companion object {
        private const val EXTRA_CART_SELECTED_ITEMS = "cartSelectedItems"
        private const val EXTRA_TOTAL_PRICE_WITHOUT_DISCOUNT = "totalPriceWithoutDiscount"

        fun start(
            context: Context,
            cartIds: List<Long>,
            totalOrderPrice: Long,
        ) {
            Intent(context, OrderActivity::class.java).apply {
                putExtra(EXTRA_CART_SELECTED_ITEMS, cartIds.toLongArray())
                putExtra(EXTRA_TOTAL_PRICE_WITHOUT_DISCOUNT, totalOrderPrice)
                context.startActivity(this)
            }
        }
    }
}
