package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.local.db.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.remote.service.NetworkModule
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.exception.handleError
import woowacourse.shopping.ui.cart.adapter.CartAdapter
import woowacourse.shopping.ui.cart.adapter.RecommendProductAdapter
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModelFactory
import woowacourse.shopping.ui.payment.PaymentActivity
import woowacourse.shopping.ui.products.toUiModel

class CartActivity : AppCompatActivity() {
    private var toast: Toast? = null
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var recommendProductAdapter: RecommendProductAdapter
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            ProductRepositoryImpl(ProductRemoteDataSourceImpl(NetworkModule.productService)),
            CartRepositoryImpl(
                CartRemoteDataSourceImpl(NetworkModule.cartItemService),
                OrderRemoteDataSourceImpl(NetworkModule.orderService),
            ),
            RecentProductRepositoryImpl.get(
                RecentProductLocalDataSourceImpl(
                    RecentProductDatabase.database().recentProductDao(),
                ),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initToolbar()
        setCartAdapter()
        setRecommendProductAdapter()
        observeCartItems()
        observeRecommendProducts()
        observeError()
        observeOrder()
    }

    private fun observeOrder() {
        viewModel.order.observe(this) { ids ->
            PaymentActivity.startActivity(this, ids)
        }
    }

    private fun observeRecommendProducts() {
        viewModel.products.observe(this) {
            recommendProductAdapter.submitList(it.map { it.toUiModel() })
        }
    }

    private fun observeError() {
        viewModel.error.observe(this) {
            val errMsg = handleError(it)
            toast = Toast.makeText(this, errMsg, Toast.LENGTH_SHORT)
            toast?.show()
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

    private fun observeCartItems() {
        viewModel.cart.observe(this) {
            adapter.submitList(it.cartItems)
        }
    }

    private fun setCartAdapter() {
        binding.rvCart.itemAnimator = null
        adapter = CartAdapter(viewModel, viewModel)
        binding.rvCart.adapter = adapter
    }

    private fun setRecommendProductAdapter() {
        binding.rvRecommend.rvRecommendProduct.itemAnimator = null
        recommendProductAdapter = RecommendProductAdapter(viewModel, viewModel)
        binding.rvRecommend.rvRecommendProduct.adapter = recommendProductAdapter
    }

    companion object {
        fun startActivity(context: Context) =
            Intent(context, CartActivity::class.java).run {
                context.startActivity(this)
            }
    }
}
