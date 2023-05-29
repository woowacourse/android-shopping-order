package woowacourse.shopping.feature.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.OrderUiModel

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun addOrders(orders: List<OrderUiModel>) {
        TODO("Not yet implemented")
    }

    override fun showOrderDetailScreen(orderId: Int) {
        TODO("Not yet implemented")
    }
}
