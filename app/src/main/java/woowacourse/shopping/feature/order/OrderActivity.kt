package woowacourse.shopping.feature.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cache.CartCache
import woowacourse.shopping.data.datasource.remote.cart.CartDataSourceImpl
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.remote.point.PointDataSourceImpl
import woowacourse.shopping.data.repository.cart.CartRemoteRepositoryImpl
import woowacourse.shopping.data.repository.order.OrderRemoteRepositoryImpl
import woowacourse.shopping.data.repository.point.PointRemoteRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.CartProductUiModel

class OrderActivity : AppCompatActivity(), OrderContract.View {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.order_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initPresenter()

        presenter.loadProducts()
        presenter.loadPayment()
    }

    private fun initPresenter() {
        presenter =
            OrderPresenter(
                this, CartRemoteRepositoryImpl(CartDataSourceImpl(), CartCache),
                PointRemoteRepositoryImpl(PointDataSourceImpl()),
                OrderRemoteRepositoryImpl(OrderDataSourceImpl())
            )
    }

    override fun initOrderProducts(orderProducts: List<CartProductUiModel>) {
        binding.recyclerviewOrderProducts.adapter = OrderProductAdapter(
            products = orderProducts
        )
    }

    override fun setUpView(point: Int, productsPrice: Int) {
        with(binding) {
            tvProductsPrice.text = getString(R.string.price_format, productsPrice)
            tvHavePoint.text = getString(R.string.holding_point, point)
            btnUseAll.setOnClickListener {
                etUsePoint.setText(point.toString())
            }
            btnOrder.text = getString(R.string.order_btn, productsPrice)

            btnOrder.setOnClickListener {
                val inputValue = binding.etUsePoint.text
                val usedPoint =
                    if (inputValue.isNullOrBlank()) 0 else inputValue.toString().toInt()
                presenter.orderProducts(usedPoint)
            }
        }

        setEditTextState(point)
    }

    override fun successOrder() {
        finish()
    }

    private fun setEditTextState(point: Int) {
        binding.etUsePoint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(inputValue: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(inputValue: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.validatePointCondition(inputValue, point)
            }

            override fun afterTextChanged(inputValue: Editable?) {
            }
        })
    }

    override fun overOwnPoint(productsPrice: Int) {
        Toast.makeText(this@OrderActivity, OVER_POINT_ERROR, Toast.LENGTH_SHORT)
            .show()
        binding.etUsePoint.text = null
        binding.btnOrder.text = getString(R.string.order_btn, productsPrice)
    }

    override fun updateTotalPriceBtn(totalPrice: Int) {
        binding.btnOrder.text =
            getString(R.string.order_btn, totalPrice)
    }

    override fun showErrorMessage(t: Throwable) {
        Toast.makeText(this, "${t.message}", Toast.LENGTH_SHORT).show()
        finish()
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
        private const val OVER_POINT_ERROR = "보유한 포인트 값을 초과합니다"
    }
}
