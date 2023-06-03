package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.ui.model.OrderUiModel
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.getSerializableCompat

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private val presenter: OrderDetailContract.Presenter by lazy {
        OrderDetailPresenter(
            view = this,
            orderRepository = OrderRepositoryImpl(
                orderRemoteDataSource = OrderRemoteDataSourceImpl()
            ),
            orderId = intent.getIntExtra(ORDER_ID, DEFAULT_VALUE),
            order = intent.getSerializableCompat(ORDER_RECORD)
        )
    }
    private val binding: ActivityOrderDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        initToolbar()
        presenter.getOrder()
    }

    private fun initToolbar() {
        binding.tbOrder.setNavigationOnClickListener {
            // 주문 목록 화면에서 온 경우에는 바로 이전 화면으로 이동
            if (intent.getIntExtra(ORDER_ID, DEFAULT_VALUE) == DEFAULT_VALUE) {

                return@setNavigationOnClickListener finish()
            }
            // 결제 화면에서 주문 상세 화면으로 온 경우에는 액티비티 종료 시 바로 shoppingActivity로 이동
            val intent = ShoppingActivity.getIntent(this)

            startActivity(intent)
        }
    }

    override fun initView(order: OrderUiModel) {
        binding.rvOrderProducts.adapter = OrderDetailRecyclerAdapter(order)
    }

    override fun showErrorMessage(errorMessage: String) {
        val intent = ShoppingActivity.getIntent(this)

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    companion object {
        private const val ORDER_ID = "order_id"
        private const val ORDER_RECORD = "order_record"
        private const val DEFAULT_VALUE = -1

        fun getIntent(
            context: Context,
            order: OrderUiModel? = null,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
                .apply {
                    order?.let {
                        putExtra(ORDER_RECORD, order)
                    }
                }

            return intent
        }

        fun getIntent(
            context: Context,
            orderId: Int?,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
                .apply {
                    orderId?.let {
                        putExtra(ORDER_ID, it)
                    }
                }

            return intent
        }
    }
}
