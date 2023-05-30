package woowacourse.shopping.ui.orderfinish

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.ui.model.Order

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private val presenter: OrderDetailContract.Presenter by lazy {
        OrderDetailPresenter(
            orderRepository = OrderRepositoryImpl(
                orderRemoteDataSource = OrderRemoteDataSourceImpl()
            ),
            orderId = 1,
            view = this
        )
    }
    private val binding: ActivityOrderDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        presenter.getOrderRecord()
    }

    override fun setUpView(order: Order) {
        binding.rvOrderProducts.adapter = OrderDetailRecyclerAdapter(order)
    }

    companion object {
        private const val ORDER_ID = "order_id"
        private const val ORDER_RECORD = "order_record"

        fun getIntent(
            context: Context,
            orderId: Int? = 1,
            order: Order? = null,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
                .apply {
                    orderId?.let {
                        putExtra(ORDER_ID, it)
                    }
                    order?.let {
                        putExtra(ORDER_RECORD, order)
                    }
                }

            return intent
        }
    }
}
