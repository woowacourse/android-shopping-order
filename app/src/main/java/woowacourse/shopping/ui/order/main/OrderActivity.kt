package woowacourse.shopping.ui.order.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiCartProducts
import woowacourse.shopping.ui.order.main.OrderContract.View

class OrderActivity : AppCompatActivity(), View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }

    override fun getOrderProducts() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val CART_PRODUCTS_KEY = "cart_products_key"

        fun newIntent(context: Context, cartProducts: List<UiCartProduct>): Intent {
            val parcelItems = UiCartProducts(cartProducts)
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(CART_PRODUCTS_KEY, parcelItems)
            return intent
        }
    }
}
