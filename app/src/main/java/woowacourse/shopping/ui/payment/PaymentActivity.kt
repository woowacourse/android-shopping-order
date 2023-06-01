package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.user.UserRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.UserRepositoryImpl
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.model.UiBasketProduct
import woowacourse.shopping.ui.model.User
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
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
        intent.getParcelableArrayExtraCompat<UiBasketProduct>(BASKET_PRODUCTS_KEY)?.let {
            presenter = PaymentPresenter(
                view = this,
                basketProducts = it.toList(),
                userRepository = UserRepositoryImpl(
                    userRemoteDateSource = UserRemoteDataSourceImpl()
                ),
                orderRepository = OrderRepositoryImpl(
                    orderRemoteDataSource = OrderRemoteDataSourceImpl()
                )
            )
        } ?: handleMissingData()
    }

    override fun initView(
        user: User,
        basketProducts: List<UiBasketProduct>,
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
                basketProducts.size - 1
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
        }
    }

    override fun showOrderDetail(orderId: Int) {
        val intent = OrderDetailActivity.getIntent(
            context = this,
            orderId = orderId
        )

        startActivity(intent)
    }

    private fun successToApplyPoint(price: Int) {
        binding.tvPaymentActualAmount.text = price.toString()
        binding.tvPaymentPointMessage.isVisible = false
        binding.btnPaymentFinalOrder.isEnabled = true
    }

    private fun failedToApplyPoint(price: Int) {
        binding.tvPaymentActualAmount.text = price.toString()
        binding.tvPaymentPointMessage.isVisible = true
        binding.btnPaymentFinalOrder.isEnabled = false
    }

    companion object {
        private const val BASKET_PRODUCTS_KEY = "basket_products"

        fun getIntent(
            context: Context,
            basketProducts: Array<UiBasketProduct>,
        ): Intent {
            val intent = Intent(context, PaymentActivity::class.java).apply {
                putExtra(BASKET_PRODUCTS_KEY, basketProducts)
            }

            return intent
        }
    }
}
