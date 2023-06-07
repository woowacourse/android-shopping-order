package woowacourse.shopping.feature.userInfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.auth.TokenSharedPreference
import woowacourse.shopping.data.datasource.remote.RetrofitClient
import woowacourse.shopping.data.datasource.remote.order.OrderService
import woowacourse.shopping.data.datasource.remote.point.PointService
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.data.repository.point.PointRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.common.load.LoadAdapter
import woowacourse.shopping.feature.orderDetail.OrderDetailActivity
import woowacourse.shopping.model.OrderUiModel
import woowacourse.shopping.model.PointUiModel

class UserInfoActivity : AppCompatActivity(), UserInfoContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: UserInfoContract.Presenter
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var loadAdapter: LoadAdapter

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()
        ConcatAdapter(config, orderHistoryAdapter, loadAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initPresenter()

        presenter.loadOrders()
        presenter.loadPoint()

        supportActionBar?.title = getString(R.string.user_page)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapter() {
        orderHistoryAdapter = OrderHistoryAdapter { showOrderDetailScreen(it) }
        loadAdapter = LoadAdapter { presenter.loadOrders() }

        binding.recyclerview.adapter = concatAdapter
    }

    private fun initPresenter() {

        val token = TokenSharedPreference.getInstance(applicationContext).getToken("") ?: ""
        val orderService = RetrofitClient.getInstanceWithToken(token)
            .create(OrderService::class.java)

        val pointService = RetrofitClient.getInstanceWithToken(token)
            .create(PointService::class.java)

        presenter = UserInfoPresenter(
            this,
            OrderRepositoryImpl(orderService),
            PointRepositoryImpl(pointService)
        )
    }

    override fun showOrders(orders: List<OrderUiModel>) {
        runOnUiThread { orderHistoryAdapter.submitList(orders) }
    }

    override fun showPoint(point: PointUiModel) {
        runOnUiThread { binding.point = point }
    }

    override fun showFailureMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showOrderDetailScreen(orderId: Int) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
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
}
