package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.orderhistory.OrderHistoryContract.View
import woowacourse.shopping.ui.orderhistory.recyclerview.adapter.OrderHistoryRecyclerViewAdapter
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.extension.showToast
import woowacourse.shopping.util.inject.injectOrderListPresenter

class OrderHistoryActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private val presenter: Presenter by lazy { injectOrderListPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater).setContentView(this)
        binding.presenter = presenter
        binding.adapter = OrderHistoryRecyclerViewAdapter(presenter::inquiryOrderDetail)
        presenter.loadMoreOrderList()
    }

    override fun showExtraOrderList(orders: List<OrderModel>) {
        binding.adapter?.submitList(orders)
    }

    override fun showLoadOrderFailed() {
        showToast(getString(R.string.load_order_failed_message))
    }

    override fun navigateToOrderDetail(order: OrderModel) {
        startActivity(OrderDetailActivity.getIntent(this, order))
    }

    override fun navigateToHome() {
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, OrderHistoryActivity::class.java)
    }
}
