package woowacourse.shopping.feature.orderDetail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.auth.TokenSharedPreference
import woowacourse.shopping.data.datasource.remote.RetrofitClient
import woowacourse.shopping.data.datasource.remote.order.OrderService
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.feature.main.MainActivity
import woowacourse.shopping.model.OrderDetailUiModel
import woowacourse.shopping.model.OrderStatusUiModel

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = TokenSharedPreference.getInstance(applicationContext).getToken("") ?: ""
        val orderService = RetrofitClient.getInstanceWithToken(token)
            .create(OrderService::class.java)

        presenter = OrderDetailPresenter(this, OrderRepositoryImpl(orderService))

        val orderId = intent.getIntExtra(ORDER_ID, -1)
        presenter.loadOrderDetail(orderId)

        supportActionBar?.title = getString(R.string.order_detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showOrderDetail(orderDetail: OrderDetailUiModel) {
        binding.apply {
            recyclerview.adapter = OrderDetailAdapter(orderDetail.orderProducts)
            item = orderDetail
            when (orderDetail.orderStatus) {
                OrderStatusUiModel.PENDING.value -> {
                    cancelOrderBtn.isEnabled = true
                    cancelOrderBtn.setOnClickListener { presenter.cancelOrder() }
                }

                else -> cancelOrderBtn.isEnabled = false
            }
        }
    }

    override fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun showFailureMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
