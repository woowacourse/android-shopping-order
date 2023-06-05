package woowacourse.shopping.view.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.impl.OrderRemoteRepository
import woowacourse.shopping.data.repository.impl.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.model.OrderDetailModel
import woowacourse.shopping.view.orderdetail.OrderDetailActivity

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {
    private val binding: ActivityOrderHistoryBinding by lazy {
        ActivityOrderHistoryBinding.inflate(
            layoutInflater,
        )
    }
    private lateinit var presenter: OrderHistoryContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpPresenter()
        setUpActionBar()
        presenter.fetchOrders()
    }

    override fun setUpPresenter() {
        presenter = OrderHistoryPresenter(this, OrderRemoteRepository(ServerPreferencesRepository(this)))
    }

    override fun showOrders(orders: List<OrderDetailModel>) {
        binding.recyclerOrders.adapter = OrderHistoryAdapter(orders) { orderId ->
            startActivity(OrderDetailActivity.newIntent(this, orderId))
        }
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showErrorMessageToast(message: String?) {
        if (message == null) {
            Toast.makeText(this, getString(R.string.notify_nothing_data), Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, OrderHistoryActivity::class.java)
        }
    }
}
