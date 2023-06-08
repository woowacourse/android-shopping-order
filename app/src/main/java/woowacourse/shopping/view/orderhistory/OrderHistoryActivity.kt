package woowacourse.shopping.view.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSource
import woowacourse.shopping.data.datasource.impl.ServerStorePreferenceDataSource
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
        val serverPreferencesRepository = ServerPreferencesRepository(
            ServerStorePreferenceDataSource(this)
        )
        presenter = OrderHistoryPresenter(this, OrderRemoteRepository(OrderRemoteDataSource(serverPreferencesRepository.getServerUrl())))
    }

    override fun showOrders(orders: List<OrderDetailModel>) {
        binding.recyclerOrders.adapter = OrderHistoryAdapter(orders) { orderId ->
            startActivity(OrderDetailActivity.newIntent(this, orderId))
        }
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showNotSuccessfulErrorToast() {
        Toast.makeText(this, getString(R.string.server_communication_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerFailureToast() {
        Toast.makeText(this, getString(R.string.server_not_response_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerResponseWrongToast() {
        Toast.makeText(this, getString(R.string.server_response_wrong), Toast.LENGTH_LONG).show()
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
