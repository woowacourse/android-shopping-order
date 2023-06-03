package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRemoteDataSourceRetrofit
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.member.MemberRemoteDataSourceRetrofit
import woowacourse.shopping.data.member.MemberRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.ui.model.CartProductModel

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initPresenter()

        setSupportActionBar(binding.orderToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupView()
    }

    private fun initBinding() {
        binding = ActivityOrderBinding.inflate(layoutInflater)
    }

    private fun initPresenter() {
        val cartRepository = CartRepositoryImpl(CartRemoteDataSourceRetrofit())
        val memberRepository = MemberRepositoryImpl(MemberRemoteDataSourceRetrofit())
        presenter = OrderPresenter(this, cartRepository, memberRepository)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {
        val ids = intent.getIntegerArrayListExtra(EXTRA_KEY_IDS) ?: return finish()
        presenter.loadProducts(ids.toList())
        presenter.loadPoints()
    }

    override fun showProducts(products: List<CartProductModel>) {
        binding.rvOrderProduct.adapter = OrderProductAdapter(products)
    }

    override fun showOriginalPrice(price: Int) {
        binding.originalPrice.text = getString(R.string.product_price, price)
    }

    override fun showPoints(points: Int) {
        binding.pointsAvailable.text = getString(R.string.points, points)
    }

    companion object {
        private const val EXTRA_KEY_IDS = "ids"

        fun createIntent(context: Context, ids: List<Int>): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putIntegerArrayListExtra(EXTRA_KEY_IDS, ArrayList(ids))
            return intent
        }
    }
}