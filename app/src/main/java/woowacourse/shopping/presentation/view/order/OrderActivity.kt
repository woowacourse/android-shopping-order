package woowacourse.shopping.presentation.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.point.PointRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.order.adapter.OrderProductAdapter
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    override lateinit var presenter: OrderContract.Presenter

    private lateinit var server: Server
    private lateinit var cartItems: List<CartModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        setPresenter()
        presenter.initReservedPoint()
        presenter.initSavingPoint()
        presenter.initCartProducts()
    }

    private fun setPresenter() {
        server = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        cartItems = intent.getParcelableArrayListExtra<CartModel>(KEY_CART_ITEMS)?.toList() ?: return finish()

        val pointRepository = PointRepositoryImpl(server)

        presenter = OrderPresenter(this, cartItems, pointRepository)
    }

    override fun setAvailablePointView(point: Int) {
        binding.tvOrderAvailablePoint.text = getString(R.string.point_text, point)
    }

    override fun setSavingPoint(point: Int) {
        binding.tvOrderPointSave.text = getString(R.string.point_text, point)
    }

    override fun setCartProductsView(products: List<CartModel>) {
        val orderProductAdapter = OrderProductAdapter()
        orderProductAdapter.setItems(products)
        binding.rvOrderProductList.adapter = orderProductAdapter
    }

    override fun handleErrorView() {
        binding.root.post {
            showToast(getString(R.string.toast_message_system_error))
        }
    }

    companion object {
        private const val KEY_CART_ITEMS = "KEY_CART_ITEMS"

        internal fun createIntent(
            context: Context,
            cartItems: List<CartModel>,
            server: Server,
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_CART_ITEMS, ArrayList(cartItems))
            intent.putExtra(KEY_SERVER_SERVER, server)

            return intent
        }
    }
}
