package woowacourse.shopping.ui.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.UiOrderedProduct
import woowacourse.shopping.model.UiPayment
import woowacourse.shopping.ui.order.detail.OrderDetailContract.View
import woowacourse.shopping.ui.order.detail.recyclerview.adapter.DetailAdapter
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.inject.injectOrderDetailPresenter

class OrderDetailActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var adapter: DetailAdapter
    private val presenter: OrderDetailContract.Presenter by lazy {
        injectOrderDetailPresenter(
            view = this,
            orderId = intent.getIntExtra(ORDER_DETAIL_KEY, 0),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater).setContentView(this)
        setActionBar()
        presenter.loadOrderDetailInfo()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.orderDetailToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showOrderDetailPaymentInfo(payment: UiPayment) {
        binding.paymentInfo = payment
    }

    override fun showOrderDetailProducts(orderedProducts: List<UiOrderedProduct>) {
        binding.rvProducts.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = DetailAdapter(orderedProducts)
        binding.rvProducts.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.navigateToHome(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToHome() {
        finish()
    }

    companion object {
        private const val ORDER_DETAIL_KEY = "order_detail_key"

        fun getIntent(context: Context, orderId: Int): Intent =
            Intent(context, OrderDetailActivity::class.java)
                .putExtra(ORDER_DETAIL_KEY, orderId)
    }
}
