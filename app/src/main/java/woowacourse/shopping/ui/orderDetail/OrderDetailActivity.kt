package woowacourse.shopping.ui.orderDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.remoteDataSourceImpl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repositoryImpl.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orders.orderItemAdapter.OrderItemAdapter
import woowacourse.shopping.ui.serverSetting.ServerSettingActivity
import woowacourse.shopping.utils.RetrofitUtil

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    private val adapter = OrderItemAdapter(
        onItemClick = { productId -> presenter.navigateToProductDetail(productId) }
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initToolbar()
        initView()
    }

    private fun initBinding() {
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initPresenter() {
        RetrofitUtil.url = ServerSettingActivity.SERVER_IO
        presenter = OrderDetailPresenter(
            this,
            OrderRepositoryImpl(
                OrderRemoteDataSourceImpl()
            ),
            intent.getLongExtra(KEY_ORDER_ID, -1)
        )
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.rvOrderProduct.adapter = adapter
        presenter.getOrderDetail(19)
    }

    override fun setOrder(order: OrderUIModel) {
        binding.order = order
        adapter.submitList(order.orderItems)
    }

    override fun navigateToProductDetail(productId: Int) {
        startActivity(DetailedProductActivity.getIntent(this, productId))
    }

    companion object {
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun getIntent(context: Context, orderId: Long): Intent =
            Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(KEY_ORDER_ID, orderId)
            }
    }
}
