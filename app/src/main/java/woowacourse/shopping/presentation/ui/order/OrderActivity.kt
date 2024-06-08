package woowacourse.shopping.presentation.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.base.BindingActivity

class OrderActivity : BindingActivity<ActivityOrderBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        title = getString(R.string.order_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getSelectedCartIds(): List<Long> = intent.getLongArrayExtra(EXTRA_CART_SELECTED_ITEMS)?.toList() ?: emptyList()

    private fun getTotalOrderPrice(): Long = intent.getLongExtra(EXTRA_TOTAL_PRICE_WITHOUT_DISCOUNT, 0)

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
