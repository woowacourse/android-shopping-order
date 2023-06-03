package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.user.UserRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.UserRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.model.BasketProductUiModel
import woowacourse.shopping.ui.model.UserUiModel
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.getParcelableArrayExtraCompat
import woowacourse.shopping.util.handleMissingData
import woowacourse.shopping.util.setImage

class PaymentActivity : AppCompatActivity(), PaymentContract.View {

    private lateinit var presenter: PaymentContract.Presenter
    private val binding: ActivityPaymentBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_payment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        initPresenter()
        presenter.getUser()
    }

    private fun initPresenter() {
        intent.getParcelableArrayExtraCompat<BasketProductUiModel>(BASKET_PRODUCTS_KEY)?.let {
            presenter = PaymentPresenter(
                view = this,
                basketProducts = it.toList(),
                userRepository = UserRepositoryImpl(
                    userRemoteDateSource = UserRemoteDataSourceImpl()
                ),
                totalPrice = intent.getIntExtra(TOTAL_PRICE_KEY, -1),
                orderRepository = OrderRepositoryImpl(
                    orderRemoteDataSource = OrderRemoteDataSourceImpl()
                )
            )
        } ?: handleMissingData()
    }

    override fun initView(
        user: UserUiModel,
        basketProducts: List<BasketProductUiModel>,
        totalPrice: Int,
    ) {
        with(binding) {
            ivPaymentProduct.setImage(
                basketProducts.first()
                    .product
                    .imageUrl
            )
            tvPaymentProductNameCount.text = getString(
                R.string.tv_order_products,
                basketProducts.first().product.name,
                basketProducts.size
            )
            tvPaymentOnImageCount.text = getString(
                R.string.iv_product_count,
                basketProducts.size
            )
            tvPaymentPointMessage.text = getString(R.string.tv_over_using_point)
            tvPaymentHoldingPoint.text = user.point.toString()
            tvPaymentTotalPrice.text = totalPrice.toString()
            tvPaymentActualAmount.text = totalPrice.toString()
            etPaymentUsingPoint.addTextChangedListener(
                InputPointWatcher(
                    totalPrice = totalPrice,
                    possessingPoint = user.point,
                    availableCase = ::successToApplyPoint,
                    inAvailableCase = ::failedToApplyPoint
                )
            )
            btnPaymentFinalOrder.setOnClickListener {
                presenter.addOrder(
                    usingPoint = etPaymentUsingPoint.text
                        .toString()
                        .toInt()
                )
            }
            ivPaymentClose.setOnClickListener {
                finish()
            }
        }
    }

    override fun showOrderDetail(orderId: Int) {
        val intent = OrderDetailActivity.getIntent(
            context = this,
            orderId = orderId
        )

        startActivity(intent)
    }

    override fun showOrderFailedMessage(message: String) {
        val intent = ShoppingActivity.getIntent(this)

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    private fun successToApplyPoint(price: Int) {
        with(binding) {
            tvPaymentActualAmount.text = price.toString()
            tvPaymentPointMessage.isVisible = false
            btnPaymentFinalOrder.isEnabled = true
        }
    }

    private fun failedToApplyPoint(price: Int) {
        with(binding) {
            tvPaymentActualAmount.text = price.toString()
            tvPaymentPointMessage.isVisible = true
            btnPaymentFinalOrder.isEnabled = false
        }
    }

    companion object {
        private const val BASKET_PRODUCTS_KEY = "basket_products"
        private const val TOTAL_PRICE_KEY = "total_price"

        fun getIntent(
            context: Context,
            totalPrice: Int,
            basketProducts: Array<BasketProductUiModel>,
        ): Intent {
            val intent = Intent(context, PaymentActivity::class.java).apply {
                putExtra(BASKET_PRODUCTS_KEY, basketProducts)
                putExtra(TOTAL_PRICE_KEY, totalPrice)
            }

            return intent
        }
    }
}
