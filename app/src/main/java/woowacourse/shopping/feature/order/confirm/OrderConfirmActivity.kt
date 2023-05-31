package woowacourse.shopping.feature.order.confirm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderConfirmBinding
import woowacourse.shopping.module.ApiModule
import woowacourse.shopping.util.getSerializableExtraCompat

class OrderConfirmActivity : AppCompatActivity(), OrderConfirmContract.View {
    private lateinit var binding: ActivityOrderConfirmBinding
    private lateinit var presenter: OrderConfirmPresenter
    private val orderConfirmAdapter: OrderConfirmAdapter by lazy { OrderConfirmAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_confirm)
        val cartIds =
            intent.getSerializableExtraCompat<ArrayList<Long>>(CART_ID_KEY) ?: return finish()
        binding.orderProductRecyclerView.adapter = orderConfirmAdapter
        initObserve()
        initPresenter(cartIds)
    }

    private fun initObserve() {
        presenter.cartProducts.observe(this) { orderConfirmAdapter.setOrderProducts(it) }
    }

    private fun initPresenter(cartIds: List<Long>) {
        presenter =
            OrderConfirmPresenter(this, CartRepositoryImpl(ApiModule.createCartService()), cartIds)
        presenter.loadSelectedCarts()
    }

    companion object {
        private const val CART_ID_KEY = "cart_id_key"
        fun getIntent(context: Context, cartIds: List<Long>): Intent {
            return Intent(context, OrderConfirmActivity::class.java).apply {
                putExtra(CART_ID_KEY, cartIds.toTypedArray())
            }
        }
    }
}
