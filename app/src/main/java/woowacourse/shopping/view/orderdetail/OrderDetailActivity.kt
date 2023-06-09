package woowacourse.shopping.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.data.BundleKeys
import woowacourse.shopping.model.data.repository.MemberRepositoryImpl
import woowacourse.shopping.model.uimodel.OrderDetailUIModel
import woowacourse.shopping.model.uimodel.OrderProductUIModel
import woowacourse.shopping.server.retrofit.RetrofitClient

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    override lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var binding: ActivityOrderDetailBinding

    private lateinit var adapter: OrderDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter()
        setAdapter()
        setView()
    }

    private fun setPresenter() {
        presenter = OrderDetailPresenter(this, MemberRepositoryImpl(RetrofitClient.membersService))
    }

    private fun setAdapter() {
        adapter = OrderDetailAdapter(emptyList())
        binding.rvOrderProducts.adapter = adapter
    }

    private fun setView() {
        val orderId = intent.getLongExtra(BundleKeys.ORDER_ID, -1)
        presenter.setOrderProducts(orderId)
    }

    override fun setOrderDetail(orderDetail: OrderDetailUIModel) {
        binding.orderDetail = orderDetail
    }

    override fun updateOrderProducts(orderItems: List<OrderProductUIModel>) {
        adapter.update(orderItems)
    }

    companion object {
        fun intent(context: Context, orderId: Long): Intent =
            Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(BundleKeys.ORDER_ID, orderId)
            }
    }
}
