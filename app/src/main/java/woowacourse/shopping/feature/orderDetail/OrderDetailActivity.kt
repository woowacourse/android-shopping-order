package woowacourse.shopping.feature.orderDetail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.OrderDetailUiModel

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = OrderDetailPresenter(this, OrderRepositoryImpl())

        val orderId = intent.getIntExtra(ORDER_ID, -1)
        presenter.loadOrderDetail(orderId)
    }

    override fun showOrderDetail(orderDetail: OrderDetailUiModel) {
        binding.apply {
            recyclerview.adapter = OrderDetailAdapter(orderDetail.orderProducts)
            recyclerview.setHasFixedSize(true)
            item = orderDetail
        }
    }

    companion object {

        private const val ORDER_ID = "ORDER_ID"

        fun getIntent(context: Context, orderId: Int): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(ORDER_ID, orderId)
            }
        }
    }
}
