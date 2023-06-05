package woowacourse.shopping.ui.ordercomplete

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    }

    companion object {
        fun from(context: Context): Intent {
            return Intent(context, OrderCompleteActivity::class.java)
        }
    }

    override fun setReceipt(receipt: Receipt) {
        TODO("Not yet implemented")
    }
}
