package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.dataSource.RemoteOrderDataSource
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.ui.orderlist.orderListAdapter.OrderProductAdapter
import woowacourse.shopping.uimodel.OrderHistoryUIModel
import woowacourse.shopping.uimodel.OrderProductUIModel

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initView()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
    }

    private fun initPresenter() {
        presenter = OrderDetailPresenter(this, OrderRepositoryImpl(RemoteOrderDataSource()))
    }

    private fun initView() {
        val orderId = intent.getIntExtra(KEY_ID, 0)
        presenter.gerOrderProducts(orderId)
    }

    override fun setOrderList(orderProducts: List<OrderProductUIModel>) {
        binding.rvOrderDetailItems.adapter = OrderProductAdapter(orderProducts)
    }

    override fun setOrderHistory(orderHistory: OrderHistoryUIModel) {
        binding.orderHistory = orderHistory
    }

    companion object {
        private const val KEY_ID = "orderId"

        fun getIntent(context: Context, orderId: Int): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra("orderId", orderId)
            }
        }
    }
}
