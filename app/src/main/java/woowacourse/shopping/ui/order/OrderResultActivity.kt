package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import woowacourse.shopping.R
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.databinding.ActivityOrderResultBinding
import woowacourse.shopping.ui.order.adapter.OrderItemListAdapter
import woowacourse.shopping.ui.order.uistate.OrderResultUIState
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.utils.PRICE_FORMAT

class OrderResultActivity : AppCompatActivity(), OrderResultContract.View {
    private val binding: ActivityOrderResultBinding by lazy {
        ActivityOrderResultBinding.inflate(layoutInflater)
    }

    private val presenter: OrderResultContract.Presenter by lazy {
        OrderResultPresenter(this, OrderRemoteRepository())
    }

    private val orderItemListAdapter: OrderItemListAdapter by lazy {
        OrderItemListAdapter {
            ProductDetailActivity.startActivity(this, it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initOrder()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showOrder(order: OrderResultUIState) {
        binding.tvTotalPrice.text =
            getString(R.string.format_price).format(PRICE_FORMAT.format(order.totalPrice))
        orderItemListAdapter.setOrder(order.orderItems)
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarOrderResult)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbarOrderResult.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbarOrderResult.navigationIcon = navigationIcon
    }

    private fun initOrder() {
        binding.rvOrderItems.adapter = orderItemListAdapter
        presenter.onLoadOrder(intent.getLongExtra(KEY_ORDER_ID, -1))
    }

    companion object {
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun startActivity(context: Context, orderId: Long) {
            val intent = Intent(context, OrderResultActivity::class.java).apply {
                putExtra(KEY_ORDER_ID, orderId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }
}
