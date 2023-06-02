package woowacourse.shopping.feature.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.OrderProductUiModel
import woowacourse.shopping.module.ApiModule

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var adapter: OrderDetailProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        val orderId: Long = intent.getLongExtra(ORDER_ID_KEY, -1L)
        presenter =
            OrderDetailPresenter(this, OrderRepositoryImpl(ApiModule.createOrderService()), orderId)

        adapter = OrderDetailProductAdapter()
        binding.orderDetailProductRecyclerView.adapter = adapter

        presenter.loadOrderInfo()
    }

    override fun setOrderProductsInfo(list: List<OrderProductUiModel>) {
        adapter.setItems(list)
    }

    override fun setOrderDate(orderDate: String) {
        binding.orderDetailDateTextView.text = orderDate
    }

    override fun setOrderPaymentInfo(
        saleBeforePrice: String,
        saleAmount: String,
        saleAfterPrice: String
    ) {
        binding.orderDetailProductTotalPriceTextView.text = saleBeforePrice
        binding.orderDetailSaleAmountTextView.text = saleAmount
        binding.totalFinalPayPriceTextView.text = saleAfterPrice
    }

    companion object {
        private const val ORDER_ID_KEY = "order_id_key"
        fun getIntent(context: Context, orderId: Long): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(ORDER_ID_KEY, orderId)
            }
        }
    }
}
