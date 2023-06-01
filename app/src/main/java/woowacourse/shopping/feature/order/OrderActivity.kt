package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.local.CartRepositoryImpl
import woowacourse.shopping.data.service.cart.CartRemoteService
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.CartProductUiModel

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderProductAdapter
    private lateinit var presenter: OrderContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        supportActionBar?.title = getString(R.string.order)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = OrderPresenter(this, CartRepositoryImpl(CartRemoteService()))
        val cartIds: List<Long> =
            intent.getLongArrayExtra(PRODUCTS_ID_KEY)?.toList() ?: listOf()
        presenter.requestProducts(cartIds)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
//                presenter.exit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val PRODUCTS_ID_KEY = "productsId"
        fun getIntent(context: Context, cartId: List<Long>): Intent {
            return Intent(context, OrderActivity::class.java)
                .putExtra(PRODUCTS_ID_KEY, cartId.toLongArray())
        }
    }

    override fun showProducts(products: List<CartProductUiModel>) {
        adapter = OrderProductAdapter(products)
        binding.recyclerviewOrderedProducts.adapter = adapter
    }
}
