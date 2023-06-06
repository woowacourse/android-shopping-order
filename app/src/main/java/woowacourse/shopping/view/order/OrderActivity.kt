package woowacourse.shopping.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.impl.MypageRemoteRepository
import woowacourse.shopping.data.repository.impl.OrderRemoteRepository
import woowacourse.shopping.data.repository.impl.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.model.OrderCartProductsModel
import woowacourse.shopping.model.OrderUserInfoModel
import woowacourse.shopping.util.getParcelableCompat
import woowacourse.shopping.view.orderdetail.OrderDetailActivity

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private val binding: ActivityOrderBinding by lazy { ActivityOrderBinding.inflate(layoutInflater) }
    private lateinit var presenter: OrderContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpActionBar()
        setUpPresenter()
        setUpBinding()
        presenter.fetchOrder()
    }

    private fun setUpPresenter() {
        val products = intent.getParcelableCompat<OrderCartProductsModel>(ORDER_PRODUCTS)
        if (products == null) {
            showDataNothingToast()
            finish()
            return
        }
        presenter = OrderPresenter(
            this,
            products,
            OrderRemoteRepository(ServerPreferencesRepository(this)),
            MypageRemoteRepository(ServerPreferencesRepository(this)),
        )
    }

    override fun showNotSuccessfulErrorToast() {
        Toast.makeText(this, getString(R.string.server_communication_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerFailureToast() {
        Toast.makeText(this, getString(R.string.server_not_response_error), Toast.LENGTH_LONG).show()
    }

    override fun showServerResponseWrongToast() {
        Toast.makeText(this, getString(R.string.server_response_wrong), Toast.LENGTH_LONG).show()
    }

    private fun setUpBinding() {
        binding.presenter = presenter
    }

    override fun showOrder(orderUserInfoModel: OrderUserInfoModel) {
        binding.recyclerOrderCartProducts.adapter =
            OrderCartProductsAdapter(orderUserInfoModel.orderProducts.orderProducts)
        binding.textUsableCash.text = getString(R.string.cash_contain_price, orderUserInfoModel.usableCash)
        binding.textUseCash.text = getString(R.string.cash_contain_price, orderUserInfoModel.afterUseCash)
        binding.textOrderPrice.text = getString(R.string.korean_won, orderUserInfoModel.paymentPrice)
    }

    override fun showUnableToast() {
        Toast.makeText(this, getString(R.string.notify_unable_order), Toast.LENGTH_LONG).show()
    }

    private fun showDataNothingToast() {
        Toast.makeText(this, getString(R.string.notify_nothing_data), Toast.LENGTH_LONG).show()
    }

    override fun showOrderComplete(orderId: Int) {
        startActivity(OrderDetailActivity.newIntent(this, orderId))
        finish()
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val ORDER_PRODUCTS = "ORDER_PRODUCT"
        fun newIntent(context: Context, selectedCartProduct: List<CartProduct>): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            val orderProducts = OrderCartProductsModel(
                selectedCartProduct.map {
                    OrderCartProductsModel.OrderCartProductModel(
                        it.id,
                        it.product.name,
                        it.product.price.price,
                        it.product.imageUrl,
                        it.quantity,
                    )
                },
            )
            intent.putExtra(ORDER_PRODUCTS, orderProducts)
            return intent
        }
    }
}
