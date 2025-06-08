package woowacourse.shopping.feature.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.data.account.AccountLocalDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.goods.repository.GoodsLocalDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRemoteDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRepositoryImpl
import woowacourse.shopping.data.payment.repository.CouponsRemoteDataSourceImpl
import woowacourse.shopping.data.payment.repository.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.payment.repository.PaymentRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.coupon.CouponServiceImpl
import woowacourse.shopping.feature.cart.recommend.RecommendFragment
import kotlin.getValue

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding

    val sharedViewModelFactory by lazy {
        CartViewModelFactory(
            CartRepositoryImpl(CartRemoteDataSourceImpl(), AccountLocalDataSourceImpl(this)),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this)),
            ),
            PaymentRepositoryImpl(CouponsRemoteDataSourceImpl(), OrderRemoteDataSourceImpl()),
            CouponServiceImpl(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .commit()
        }

        val viewModel = ViewModelProvider(this, sharedViewModelFactory)[CartViewModel::class.java]

        viewModel.appBarTitle.observe(this) { title ->
            supportActionBar?.title = title
        }
        viewModel.orderSuccessEvent.observe(this) {
            showToastMessage(getString(R.string.order_payment_success_alert))
            finish()
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigateToRecommend() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, RecommendFragment())
            .addToBackStack("CartFragment")
            .commit()
    }

    fun navigateToOrder() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, OrderFragment())
            .addToBackStack("OrderFragment")
            .commit()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
