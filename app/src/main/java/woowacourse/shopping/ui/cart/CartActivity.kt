package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.cart.cartitem.CartItemFragment
import woowacourse.shopping.ui.cart.recommend.RecommendFragment
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModelFactory
import woowacourse.shopping.ui.coupon.CouponActivity

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            ProductRepositoryImpl(),
            CartRepositoryImpl(),
            RecentProductRepositoryImpl.get(RecentProductDatabase.database().recentProductDao()),
            OrderRepositoryImpl(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initToolbar()
        initFragment()

        onClickOrderBtn()
        onClickTotalCheckBox()
    }

    private fun initFragment() {
        supportFragmentManager.findFragmentById(R.id.fcv_cart) ?: supportFragmentManager.commit {
            replace(R.id.fcv_cart, CartItemFragment())
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun initToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun onClickOrderBtn() {
        binding.layoutOrder.btnOrder.setOnClickListener {
            if (supportFragmentManager.findFragmentById(R.id.fcv_cart) is CartItemFragment) {
                supportFragmentManager.commit {
                    add(R.id.fcv_cart, RecommendFragment())
                    addToBackStack(null)
                }
            } else if (supportFragmentManager.findFragmentById(R.id.fcv_cart) is RecommendFragment) {
                startActivity(
                    CouponActivity.newIntent(
                        this,
                        viewModel.cart.value?.cartItems?.filter { it.isChecked } ?: emptyList(),
                    ),
                )
            }
        }
    }

    private fun onClickTotalCheckBox() {
        binding.cbCartItemTotal.setOnClickListener {
            viewModel.totalCheckBoxCheck((it as CheckBox).isChecked)
        }
    }

    companion object {
        fun startActivity(context: Context) =
            Intent(context, CartActivity::class.java).run {
                context.startActivity(this)
            }
    }
}
