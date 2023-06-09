package woowacourse.shopping.view.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.model.data.repository.MemberRepositoryImpl
import woowacourse.shopping.model.uimodel.OrderUIModel
import woowacourse.shopping.server.retrofit.RetrofitClient
import woowacourse.shopping.view.orderdetail.OrderDetailActivity

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    override lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var binding: ActivityOrderHistoryBinding

    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter()
        setAdapter()
    }

    private fun setPresenter() {
        presenter = OrderHistoryPresenter(this, MemberRepositoryImpl(RetrofitClient.membersService))
    }

    private fun setAdapter() {
        adapter = OrderHistoryAdapter(
            emptyList(),
            object : OrderHistoryClickListener {
                override fun orderItemOnClick(order: OrderUIModel) {
                    showOrderDetail(order)
                }
            }
        )
        binding.rvOrderHistory.adapter = adapter
    }

    override fun updateOrders(orders: List<OrderUIModel>) {
        adapter.update(orders)
    }

    private fun showOrderDetail(order: OrderUIModel) {
        val intent = OrderDetailActivity.intent(this, order.orderId)
        startActivity(intent)
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, OrderHistoryActivity::class.java)
        }
    }
}
