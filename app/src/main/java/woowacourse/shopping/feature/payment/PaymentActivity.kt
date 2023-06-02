package woowacourse.shopping.feature.payment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.auth.TokenSharedPreference
import woowacourse.shopping.data.datasource.remote.RetrofitClient
import woowacourse.shopping.data.datasource.remote.cart.CartApi
import woowacourse.shopping.data.datasource.remote.cart.CartRetrofitService
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.point.PointRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PointUiModel

class PaymentActivity : AppCompatActivity(), PaymentContract.View {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var presenter: PaymentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPresenter()
        val cartIds = intent.getIntegerArrayListExtra(CART_ITEM_IDS)
        presenter.loadCartProducts(cartIds?.toList() ?: emptyList())
        setup()
    }

    private fun initPresenter() {
        val token = TokenSharedPreference.getInstance(applicationContext).getToken("") ?: ""

        val cartApi = RetrofitClient.getInstanceWithToken(token)
            .create(CartApi::class.java)

        presenter = PaymentPresenter(
            this,
            CartRepositoryImpl(CartRetrofitService(cartApi)),
            PointRepositoryImpl()
        )
    }

    private fun setup() {
        binding.orderPriceTv.text = resources.getString(R.string.price_format)
            .format(intent.getIntExtra(TOTAL_PRICE, 0))
        presenter.loadPoint()
    }

    override fun showCartProducts(cartProducts: List<CartProductUiModel>) {
        binding.recyclerview.adapter = PaymentAdapter(cartProducts)
    }

    override fun showPoint(point: PointUiModel) {
        binding.currentPointTv.text = getString(R.string.current_point).format(point.currentPoint)
    }

    companion object {

        private const val CART_ITEM_IDS = "CART_ITEM_IDS"
        private const val TOTAL_PRICE = "TOTAL_PRICE"

        fun getIntent(context: Context, cartItemIds: ArrayList<Int>, totalPrice: Int): Intent {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putIntegerArrayListExtra(CART_ITEM_IDS, cartItemIds)
            intent.putExtra(TOTAL_PRICE, totalPrice)
            return intent
        }
    }
}
