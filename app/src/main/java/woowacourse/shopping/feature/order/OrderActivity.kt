package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.Cart
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.R
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.model.CartState
import woowacourse.shopping.model.mapper.toDomain

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private val presenter: OrderContract.Presenter by lazy {
        val orderProducts: CartState by lazy { intent.getParcelableExtra(ORDER_PRODUCTS_KEY)!! }
        val orderRepository: OrderRepository = OrderRepositoryImpl()
        OrderPresenter(
            view = this,
            orderProducts = orderProducts.toDomain(),
            orderRepository = orderRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }

    override fun setOrderProducts(orderProducts: Cart) {
        TODO("Not yet implemented")
    }

    override fun setProductsSum(productsSum: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val ORDER_PRODUCTS_KEY = "order_products_key"

        fun startActivity(context: Context, orderProducts: CartState) {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(ORDER_PRODUCTS_KEY, orderProducts)
            context.startActivity(intent)
        }
    }
}
