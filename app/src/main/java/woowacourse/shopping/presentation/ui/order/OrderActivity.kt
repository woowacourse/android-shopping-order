package woowacourse.shopping.presentation.ui.order

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.data.defaultRepository.DefaultOrderRepository
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.presentation.ui.order.detail.OrderDetailActivity

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderAdapter
    private val presenter: OrderPresenter by lazy { OrderPresenter(this, DefaultOrderRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        presenter.fetchOrders()
    }

    private fun initAdapter() {
        adapter = OrderAdapter { presenter.selectOrder(it) }
        binding.recyclerOrders.adapter = adapter
        binding.recyclerOrders.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom = 20
            }
        })
    }

    override fun showOrders(orders: List<Order>) {
        adapter.submitList(orders)
    }

    override fun showError(message: String) {
        Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
        Log.e(TAG, message)
    }

    override fun showOrderDetail(orderId: Long) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
    }

    companion object {
        const val TAG = "OrderActivity"
    }
}
