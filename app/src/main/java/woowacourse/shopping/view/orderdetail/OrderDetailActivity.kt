package woowacourse.shopping.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.impl.OrderRemoteRepository
import woowacourse.shopping.data.repository.impl.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.OrderDetailModel

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private val binding: ActivityOrderDetailBinding by lazy {
        ActivityOrderDetailBinding.inflate(
            layoutInflater,
        )
    }
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
        setUpPresenter()
        presenter.fetchOrder()
    }

    override fun showOrderDetail(orderDetailModel: OrderDetailModel) {
        binding.recyclerOrderProducts.adapter = OrderProductsAdapter(orderDetailModel.products)
        binding.textOrderPrice.text = getString(R.string.korean_won, orderDetailModel.totalPrice)
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpPresenter() {
        val orderId = intent.getIntExtra(ORDER_ID, -1)
        if (orderId == -1) {
            showDataNothingToast()
            finish()
        }
        presenter = OrderDetailPresenter(
            orderId,
            this,
            OrderRemoteRepository(
                ServerPreferencesRepository(this),
            ),
        )
    }

    override fun showNotSuccessfulErrorToast() {
        Toast.makeText(this, getString(R.string.server_communication_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerFailureToast() {
        Toast.makeText(this, getString(R.string.server_not_response_error), Toast.LENGTH_LONG).show()
    }

    private fun showDataNothingToast() {
        Toast.makeText(this, getString(R.string.notify_nothing_data), Toast.LENGTH_LONG).show()
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
        private const val ORDER_ID = "ORDER_ID"
        fun newIntent(context: Context, orderId: Int): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(ORDER_ID, orderId)
            return intent
        }
    }
}
