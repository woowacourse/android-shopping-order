package woowacourse.shopping.feature.payment

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.auth.TokenSharedPreference
import woowacourse.shopping.data.datasource.remote.RetrofitClient
import woowacourse.shopping.data.datasource.remote.cart.CartService
import woowacourse.shopping.data.datasource.remote.cart.CartDataSourceImpl
import woowacourse.shopping.data.repository.cart.CartRepositoryImpl
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.data.repository.point.PointRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.feature.main.MainActivity
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

        supportActionBar?.title = getString(R.string.payment_page)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPresenter() {
        val token = TokenSharedPreference.getInstance(applicationContext).getToken("") ?: ""

        val cartService = RetrofitClient.getInstanceWithToken(token)
            .create(CartService::class.java)

        presenter = PaymentPresenter(
            this,
            CartRepositoryImpl(CartDataSourceImpl(cartService)),
            PointRepositoryImpl(),
            OrderRepositoryImpl()
        )
    }

    private fun setup() {
        binding.orderPriceTv.text = getString(R.string.price_format)
            .format(intent.getIntExtra(TOTAL_PRICE, 0))
        binding.recyclerview.setHasFixedSize(true)

        presenter.loadPoint()

        binding.payButtonTv.text =
            getString(R.string.final_pay_amount).format(intent.getIntExtra(TOTAL_PRICE, 0))
        binding.payButtonTv.setOnClickListener {
            presenter.placeOrder(binding.pointEt.text.toString().toIntOrNull() ?: 0)
        }

        binding.allPointBtn.setOnClickListener {
            presenter.useAllPoint()
        }
    }

    override fun showCartProducts(cartProducts: List<CartProductUiModel>) {
        binding.recyclerview.adapter = PaymentAdapter(cartProducts)
    }

    override fun showPoint(point: PointUiModel) {
        binding.currentPointTv.text = getString(R.string.current_point).format(point.currentPoint)
        setEditTextState(point, intent.getIntExtra(TOTAL_PRICE, 0))
    }

    override fun showPaymentDoneScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun setPoint(usedPoint: Int) {
        binding.pointEt.setText(usedPoint.toString(), TextView.BufferType.EDITABLE)
    }

    private fun setEditTextState(point: PointUiModel, productsPrice: Int) {
        binding.pointEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(inputValue: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val inputPoint =
                    if (inputValue.isNullOrBlank()) 0
                    else inputValue.toString().toInt()

                if (inputPoint > point.currentPoint) {
                    Toast.makeText(this@PaymentActivity, "보유한 포인트 값을 초과합니다", Toast.LENGTH_SHORT)
                        .show()
                    binding.pointEt.text = null
                    binding.payButtonTv.text =
                        getString(R.string.final_pay_amount).format(productsPrice)
                } else {
                    binding.payButtonTv.text =
                        getString(R.string.final_pay_amount).format(productsPrice - inputPoint)
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
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
