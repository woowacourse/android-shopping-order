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
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.goods.repository.GoodsLocalDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRemoteDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.feature.cart.recommend.RecommendFragment

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel

    val sharedViewModelFactory by lazy {
        CartViewModelFactory(
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this)),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = CartViewModelFactory(
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this))
            )
        )
        viewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]

        observeUiEvent()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .commit()
        }
    }

    private fun observeUiEvent() {
        viewModel.event.observe(this) { event ->
            when (event) {
                is CartUiEvent.ShowToast -> {
                    Toast.makeText(this, getStringByKey(event.key), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun getStringByKey(key: ToastMessageKey): String {
        return when (key) {
            ToastMessageKey.FAIL_INCREASE -> getString(R.string.toast_fail_increase)
            ToastMessageKey.FAIL_DECREASE -> getString(R.string.toast_fail_decrease)
            ToastMessageKey.FAIL_DELETE -> getString(R.string.toast_fail_delete)
            ToastMessageKey.FAIL_SELECT_ALL -> getString(R.string.toast_fail_select_all)
            ToastMessageKey.FAIL_LOGIN -> getString(R.string.toast_fail_login)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
            return true
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

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
