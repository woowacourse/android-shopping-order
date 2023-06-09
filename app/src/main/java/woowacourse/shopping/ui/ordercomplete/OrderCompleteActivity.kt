package woowacourse.shopping.ui.ordercomplete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ordercomplete.OrderCompleteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistoryDataSourceImpl
import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderCompleteBinding
import woowacourse.shopping.ui.order.adapter.OrderAdapter
import woowacourse.shopping.ui.ordercomplete.presenter.OrderCompleteContract
import woowacourse.shopping.ui.ordercomplete.presenter.OrderCompletePresenter
import woowacourse.shopping.ui.ordercomplete.uimodel.Bill
import woowacourse.shopping.ui.orderhistory.OrderHistoryActivity

class OrderCompleteActivity : AppCompatActivity(), OrderCompleteContract.View {
    private lateinit var binding: ActivityOrderCompleteBinding
    private val presenter: OrderCompleteContract.Presenter by lazy { initPresenter() }
    private lateinit var orderAdapter: OrderAdapter

    private fun initPresenter() =
        OrderCompletePresenter(
            this,
            OrderRepositoryImpl(
                OrderDataSourceImpl(
                    ServiceFactory.orderService,

                ),
                OrderCompleteDataSourceImpl(
                    ServiceFactory.orderCompleteService,

                ),
                OrderHistoryDataSourceImpl(
                    ServiceFactory.orderHistoryService,

                ),
            ),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToOrderHistoryView()
        initView()
    }

    private fun initView() {
        presenter.getReceipt(intent.getIntExtra(ORDER_ID, 0))
    }

    private fun navigateToOrderHistoryView() {
        binding.tvOrderCompleteHistoryBtn.setOnClickListener {
            startActivity(OrderHistoryActivity.from(this))
            finish()
        }
    }

    override fun setReceipt(bill: Bill) {
        with(binding) {
            initAdapter(bill)
            receipt = bill
        }
    }

    private fun ActivityOrderCompleteBinding.initAdapter(bill: Bill) {
        orderAdapter = OrderAdapter(bill.orderProducts)
        rvOrderCompleteList.adapter = orderAdapter
    }

    companion object {
        private const val ORDER_ID = "ORDER_ID"
        fun from(context: Context, orderId: Int): Intent {
            return Intent(context, OrderCompleteActivity::class.java).apply {
                putExtra(ORDER_ID, orderId)
            }
        }
    }
}
