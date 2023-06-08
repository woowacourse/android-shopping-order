package woowacourse.shopping.presentation.view.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.RetrofitBuilder
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSourceImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.view.orderdetail.OrderDetailActivity
import woowacourse.shopping.presentation.view.orderlist.adapter.OrderListAdapter
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    private lateinit var retrofitBuilder: RetrofitBuilder

    private lateinit var presenter: OrderListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)

        url = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        token = intent.getSerializableCompat(KEY_SERVER_TOKEN) ?: return finish()
        retrofitBuilder = RetrofitBuilder.getInstance(url, token)

        setToolbar()
        setPresenter()
        setDecorationOrderList()
        presenter.loadOrderList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return true
    }

    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.toolbar_title_order_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setPresenter() {
        val orderRemoteDataSource = OrderRemoteDataSourceImpl(retrofitBuilder.createOrderService())
        presenter = OrderListPresenter(
            this,
            orderRepository = OrderRepositoryImpl(orderRemoteDataSource),
        )
    }

    private fun setDecorationOrderList() {
        val decoration = DividerItemDecoration(binding.rvOrderList.context, LinearLayoutManager(this).orientation)
        binding.rvOrderList.addItemDecoration(decoration)
    }
    override fun showOrderListItemView(orders: List<OrderDetailModel>) {
        binding.rvOrderList.post {
            binding.rvOrderList.adapter = OrderListAdapter(orders, ::onOrderItemClick)
        }
    }

    private fun onOrderItemClick(orderId: Long) {
        val intent = OrderDetailActivity.createIntent(this, orderId, url, token)
        startActivity(intent)
    }

    override fun handleErrorView(messageId: Int) {
        binding.root.post {
            showToast(getString(messageId))
        }
    }

    companion object {
        fun createIntent(context: Context, url: Server.Url, token: Server.Token): Intent {
            val intent = Intent(context, OrderListActivity::class.java)
            intent.putExtra(KEY_SERVER_SERVER, url)
            intent.putExtra(KEY_SERVER_TOKEN, token)

            return intent
        }
    }
}
