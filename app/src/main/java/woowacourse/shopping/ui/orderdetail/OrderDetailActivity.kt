package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.orderdetail.OrderDetailSourceImpl
import woowacourse.shopping.data.repository.OrderDetailRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.OrderProductUIModel
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orderdetail.contract.OrderDetailContract
import woowacourse.shopping.ui.orderdetail.contract.presenter.OrderDetailPresenter

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        presenter = OrderDetailPresenter(
            this,
            OrderDetailRepositoryImpl(OrderDetailSourceImpl(AuthInfoDataSourceImpl.getInstance(this))),
        )
        presenter.getOrderDetail(intent.getLongExtra("KEY_ORDER_ID", 0))
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tbOrderDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun setOrderDetail(order: OrderUIModel) {
        setOrderProducts(order.orderProducts)
        binding.order = order
    }

    private fun setOrderProducts(products: List<OrderProductUIModel>) {
        binding.rvOrderDetail.adapter = OrderDetailAdapter(products)
    }

    companion object {
        fun from(
            context: Context,
            id: Long,
        ): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra("KEY_ORDER_ID", id)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}
