package woowacourse.shopping.presentation.ui.order

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.data.defaultRepository.DefaultOrderRepository
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.domain.model.Order

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var adapter: OrderDetailAdapter
    private val presenter: OrderDetailContract.Presenter by lazy {
        OrderDetailPresenter(this, DefaultOrderRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        presenter.fetchOrderDetail(intent.getLongExtra(ORDER_ID, -1))
    }

    private fun initAdapter() {
        adapter = OrderDetailAdapter()
        binding.recyclerOrderOrder.adapter = adapter
        binding.recyclerOrderOrder.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

    override fun showOrderDateTime(dateTime: String) {
        binding.textOrderDateTimeValue.text = dateTime
    }

    override fun showOrderDetail(orderDetail: List<Order.OrderedProduct>) {
        adapter.submitList(orderDetail)
    }

    override fun showUnexpectedError() {
        Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ORDER_ID = "ORDER_ID"

        fun getIntent(context: Context, orderId: Long): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(ORDER_ID, orderId)
            }
        }
    }
}
