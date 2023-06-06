package woowacourse.shopping.ui.ordercomplete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.model.Receipt
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ordercomplete.OrderCompleteDataSourceImpl
import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderCompleteBinding
import woowacourse.shopping.ui.ordercomplete.presenter.OrderCompleteContract
import woowacourse.shopping.ui.ordercomplete.presenter.OrderCompletePresenter

class OrderCompleteActivity : AppCompatActivity(), OrderCompleteContract.View {

    private lateinit var binding: ActivityOrderCompleteBinding
    private val presenter: OrderCompleteContract.Presenter by lazy { initPresenter() }

    private fun initPresenter() =
        OrderCompletePresenter(
            this,
            OrderRepositoryImpl(
                OrderDataSourceImpl(
                    ServiceFactory.orderService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
                OrderCompleteDataSourceImpl(
                    ServiceFactory.orderCompleteService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
            ),

        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.getReceipt(intent.getIntExtra(ORDER_ID, 0))
    }

    companion object {
        private const val ORDER_ID = "ORDER_ID"
        fun from(context: Context, orderId: Int): Intent {
            return Intent(context, OrderCompleteActivity::class.java).apply {
                putExtra(ORDER_ID, orderId)
            }
        }
    }

    override fun setReceipt(receipt: Receipt) {
        Log.d("123OrderCompleteActivity", receipt.toString())
    }
}
