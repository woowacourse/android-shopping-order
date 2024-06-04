package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.cart.cartList.CartListFragment
import woowacourse.shopping.presentation.ui.cart.recommend.RecommendFragment
import woowacourse.shopping.presentation.ui.model.UpdatedProductData
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import woowacourse.shopping.presentation.util.EventObserver

class CartActivity : BindingActivity<ActivityCartBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_cart

    private val viewModel: CartViewModel by viewModels {
        val initialItemQuantity = intent.getIntExtra(EXTRA_CART_ITEM_QUANTITY, 0)
        CartViewModel.Companion.Factory(initialItemQuantity)
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.cartHandler = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initActionBarTitle()
        observeOrderEvent()
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            CartListFragment(),
        ).commit()
    }

    private fun initActionBarTitle() {
        title = getString(R.string.cart_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun observeOrderEvent() {
        viewModel.orderEvent.observe(
            this,
            EventObserver {
                when (it) {
                    OrderEvent.CompleteOrder -> {
                        showToast("상품이 성공적으로 주문되었습니다!")
                        val updatedProducts = generateUpdateProducts()
                        ShoppingActivity.startWithNewProductQuantities(this, updatedProducts)
                        finish()
                    }

                    OrderEvent.MoveToRecommend -> {
                        supportFragmentManager.beginTransaction().replace(
                            R.id.fragment_container,
                            RecommendFragment(),
                        ).addToBackStack(null).commit()
                    }
                }
            },
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val updatedProducts = generateUpdateProducts()
        ShoppingActivity.startWithNewProductQuantities(this, updatedProducts)
        finish()
        return true
    }

    private fun generateUpdateProducts(): List<UpdatedProductData> {
        val updatedProducts =
            viewModel.changedCartProducts.entries.map { (productId, quantity) ->
                UpdatedProductData(productId, quantity)
            }
        return updatedProducts
    }

    companion object {
        private const val EXTRA_CART_ITEM_QUANTITY = "cartItemQuantity"

        fun startWithResultLauncher(
            context: Context,
            cartItemQuantity: Int,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, CartActivity::class.java).apply {
                putExtra(EXTRA_CART_ITEM_QUANTITY, cartItemQuantity)
                activityLauncher.launch(this)
            }
        }
    }
}
