package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.local.AuthInfoLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistoryRemoteSourceImpl
import woowacourse.shopping.data.repository.OrderHistoryRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.orderhistory.contract.OrderHistoryContract
import woowacourse.shopping.ui.orderhistory.contract.presenter.OrderHistoryPresenter

class OrderHistoryActivity :
    AppCompatActivity(),
    OrderHistoryContract.View,
    OnHistoryOrderListener {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var presenter: OrderHistoryContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        presenter = OrderHistoryPresenter(
            this,
            OrderHistoryRepositoryImpl(
                OrderHistoryRemoteSourceImpl(),
            ),
        )
        presenter.getOrderHistory()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tbOrderHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun setOrderHistory(orders: List<OrderUIModel>) {
        binding.rvOrderHistory.adapter = OrderHistoryAdapter(orders, this)
    }

    override fun onHistoryOrderClick(id: Long) {
        startActivity(OrderDetailActivity.from(this, id))
    }

    companion object {
        fun from(context: Context): Intent {
            return Intent(context, OrderHistoryActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}
