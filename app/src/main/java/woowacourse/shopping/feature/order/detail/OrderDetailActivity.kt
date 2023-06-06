package woowacourse.shopping.feature.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.order.OrderProduct
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.R
import woowacourse.shopping.ServerType
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.order.OrderState
import woowacourse.shopping.util.extension.showToast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private var _binding: ActivityOrderDetailBinding? = null
    private val binding: ActivityOrderDetailBinding get() = _binding!!

    private val presenter: OrderDetailContract.Presenter by lazy {
        val orderId: Long = intent.getLongExtra(ORDER_ID_KEY, 0L)
        val serverUrl: String = intent.getStringExtra(ServerType.INTENT_KEY)!!
        val orderRepository: OrderRepository = OrderRemoteRepository(url = serverUrl)
        OrderDetailPresenter(this, orderId, orderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun setViewFixedContents(order: OrderState) {
        binding.order = order
    }

    override fun setProductsSummary(firstProductName: String, productsCount: Int, originalPrice: Int) {
        binding.productsSummaryTv.text = "$firstProductName 외 $productsCount ${originalPrice}원"
    }

    override fun setOrderDate(orderDate: String) {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

        val dateTime = LocalDateTime.parse(orderDate, inputFormat)
        binding.productsSummaryTv.text = dateTime.format(outputFormat)
    }

    override fun setOrderNumber(number: Long) {
        binding.orderNumber.text = number.toString()
    }

    override fun setOrderProducts(orderProducts: List<OrderProduct>) {
        // todo adapter stting
    }

    override fun showAccessError() {
        showToast(getString(R.string.error_intent_message))
    }

    override fun closeOrderDetail() {
        finish()
    }

    companion object {
        private const val ORDER_ID_KEY = "order_id_key"

        fun startActivity(context: Context, serverUrl: String, orderId: Long) {
            val intent: Intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(ServerType.INTENT_KEY, serverUrl)
            intent.putExtra(ORDER_ID_KEY, orderId)
            context.startActivity(intent)
        }
    }
}
