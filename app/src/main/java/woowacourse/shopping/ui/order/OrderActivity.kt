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
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.ui.order.adapter.OrderItemListAdapter
import woowacourse.shopping.ui.order.uistate.OrderUIState
import woowacourse.shopping.ui.products.uistate.ProductUIState
import woowacourse.shopping.utils.PRICE_FORMAT

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }

    private val presenter: OrderContract.Presenter by lazy {
        OrderPresenter(this, OrderRemoteRepository())
    }

    private val orderItemListAdapter: OrderItemListAdapter by lazy {
        OrderItemListAdapter {
            presenter.onLoadProduct(it)
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

    override fun showOrder(order: OrderUIState) {
        binding.tvTotalPrice.text =
            getString(R.string.format_price).format(PRICE_FORMAT.format(order.totalPrice))
        orderItemListAdapter.setOrder(order.orderItems)
    }

    override fun showProduct(product: ProductUIState) {
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarOrder)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbarOrder.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbarOrder.navigationIcon = navigationIcon
    }

    private fun initOrder() {
        binding.rvOrderItems.adapter = orderItemListAdapter
        presenter.onLoadOrder(intent.getLongExtra(KEY_ORDER_ID, -1))
    }

    companion object {
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun startActivity(context: Context, orderId: Long) {
            val intent = Intent(context, OrderActivity::class.java).apply {
                putExtra(KEY_ORDER_ID, orderId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }
}
