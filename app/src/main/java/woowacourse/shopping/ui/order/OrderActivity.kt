package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.PriceModel
import woowacourse.shopping.ui.order.OrderContract.View
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.adapter.OrderRecyclerViewAdapter
import woowacourse.shopping.util.extension.getParcelableExtraCompat
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.extension.showSnackBar
import woowacourse.shopping.util.extension.showToast
import woowacourse.shopping.util.inject.injectOrderPresenter

class OrderActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderBinding
    private val presenter: OrderContract.Presenter by lazy {
        injectOrderPresenter(
            view = this,
            order = intent.getParcelableExtraCompat(ORDER_KEY)!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater).setContentView(this)
        binding.presenter = presenter
        binding.adapter = OrderRecyclerViewAdapter(onApplyPoint = presenter::applyPoint)
        presenter.fetchAll()
    }

    override fun showMoreOrders(orderItems: List<ListItem>) {
        binding.adapter?.update(orderItems)
    }

    override fun showOrderLoadFailed() {
        showToast(getString(R.string.order_load_failed_message))
    }

    override fun showOrderCompleted() {
        binding.root.showSnackBar(R.string.order_success_message)
    }

    override fun showOrderFailed() {
        showToast(getString(R.string.order_failed_message))
    }

    override fun showFinalPayment(totalPayment: PriceModel) {
        binding.totalPayment = totalPayment
    }

    override fun navigateToHome() {
        setResult(RESULT_OK)
        finish()
    }

    companion object {
        private const val ORDER_KEY = "order_key"

        fun getIntent(context: Context, order: OrderModel): Intent {
            return Intent(context, OrderActivity::class.java)
                .putExtra(ORDER_KEY, order)
        }
    }
}
