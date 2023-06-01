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
import woowacourse.shopping.ui.mapper.toDomain
import woowacourse.shopping.ui.model.UiBasketProduct
import woowacourse.shopping.ui.model.UiUserPointInfo
import woowacourse.shopping.util.getParcelableArrayListExtraCompat
import woowacourse.shopping.util.intentDataNullProcess

class PaymentConfirmActivity : AppCompatActivity(), PaymentConfirmContract.View {

    private lateinit var binding: ActivityPaymentConfirmBinding
    private lateinit var presenter: PaymentConfirmContract.Presenter
    private lateinit var currentOrderBasketProducts: List<UiBasketProduct>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_confirm)
        if (!initExtraData()) return
        initPresenter()
    }

    private fun initExtraData(): Boolean {
        currentOrderBasketProducts =
            intent.getParcelableArrayListExtraCompat<UiBasketProduct>(CURRENT_ORDER_BASKET_PRODUCTS)
                ?.toList() ?: return intentDataNullProcess(CURRENT_ORDER_BASKET_PRODUCTS)
        return true
    }

    private fun initPresenter() {
        presenter = PaymentConfirmPresenter(
            this,
            PointRepository(RemoteUserPointInfoDataSource()),
            currentOrderBasketProducts.map { it.toDomain() }
        )
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
