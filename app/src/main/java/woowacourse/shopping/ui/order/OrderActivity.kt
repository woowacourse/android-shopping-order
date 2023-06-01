package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.repositoryImpl.CartRepositoryImpl
import woowacourse.shopping.service.RemoteCartService
import woowacourse.shopping.utils.ServerURL

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initOrderItems()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
    }

    private fun initPresenter() {
        presenter = OrderPresenter(this, CartRepositoryImpl(RemoteCartService(ServerURL.url)))
    }

    private fun initOrderItems() {
        val ids: List<Int> = intent.getIntegerArrayListExtra(KEY_ORDER_ID_LIST)?.toList() ?: return
        presenter.getOrderInfo(ids)
    }

    companion object {
        private const val KEY_ORDER_ID_LIST = "order_list"

        fun getIntent(context: Context, ids: ArrayList<Int>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putIntegerArrayListExtra(KEY_ORDER_ID_LIST, ids)
            }
        }
    }
}
