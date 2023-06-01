package woowacourse.shopping.ui.paymentconfirm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.userpointdata.remote.RemoteUserPointInfoDataSource
import woowacourse.shopping.data.repository.PointRepository
import woowacourse.shopping.databinding.ActivityPaymentConfirmBinding
import woowacourse.shopping.ui.model.UiBasketProduct
import woowacourse.shopping.ui.model.UiUserPointInfo

class PaymentConfirmActivity : AppCompatActivity(), PaymentConfirmContract.View {

    private lateinit var binding: ActivityPaymentConfirmBinding
    private lateinit var presenter: PaymentConfirmContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_confirm)
        initPresenter()
    }

    private fun initPresenter() {
        presenter = PaymentConfirmPresenter(this, PointRepository(RemoteUserPointInfoDataSource()))
    }

    override fun updateUserPointInfo(userPointInfo: UiUserPointInfo) {
        binding.userPointInfo = userPointInfo
    }

    companion object {
        private const val CURRENT_ORDER_BASKET_PRODUCTS = "currentOrderBasketProducts"

        fun getIntent(context: Context, basketProducts: List<UiBasketProduct>): Intent =
            Intent(context, PaymentConfirmActivity::class.java).apply {
                putParcelableArrayListExtra(
                    CURRENT_ORDER_BASKET_PRODUCTS,
                    ArrayList(basketProducts)
                )
            }
    }
}
