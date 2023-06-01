package woowacourse.shopping.ui.orders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.databinding.ActivityOrdersBinding
import woowacourse.shopping.ui.order.OrderResultActivity
import woowacourse.shopping.ui.orders.adapter.OrderListAdapter
import woowacourse.shopping.ui.orders.uistate.OrdersItemUIState

class OrdersActivity : AppCompatActivity(), OrdersContract.View {
    private val binding by lazy {
        ActivityOrdersBinding.inflate(layoutInflater)
    }

    private val presenter by lazy {
        OrdersPresenter(
            this,
            OrderRemoteRepository()
        )
    }

    private val orderListAdapter by lazy {
        OrderListAdapter {
            OrderResultActivity.startActivity(this, it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initOrders()
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

    override fun showOrders(orders: List<OrdersItemUIState>) {
        orderListAdapter.setOrders(orders)
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarOrders)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbarOrders.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbarOrders.navigationIcon = navigationIcon
    }

    private fun initOrders() {
        binding.rvOrderList.adapter = orderListAdapter
        presenter.onLoadOrders()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OrdersActivity::class.java)
            context.startActivity(intent)
        }
    }
}
