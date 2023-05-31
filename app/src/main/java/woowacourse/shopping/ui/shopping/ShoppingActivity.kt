package woowacourse.shopping.ui.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import woowacourse.shopping.data.ShoppingRetrofit
import woowacourse.shopping.data.cart.CartItemRemoteService
import woowacourse.shopping.data.cart.DefaultCartItemRepository
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.product.DefaultProductRepository
import woowacourse.shopping.data.product.ProductRemoteService
import woowacourse.shopping.data.recentlyviewedproduct.DefaultRecentlyViewedProductRepository
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductMemoryDao
import woowacourse.shopping.data.user.DefaultUserRepository
import woowacourse.shopping.data.user.UserMemorySource
import woowacourse.shopping.data.user.UserRemoteSource
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.order.orderlist.OrderListActivity
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.shopping.adapter.RecentlyViewedProductListAdapter
import woowacourse.shopping.ui.shopping.adapter.ShoppingAdapter
import woowacourse.shopping.ui.shopping.uistate.ProductUIState
import woowacourse.shopping.ui.shopping.uistate.RecentlyViewedProductUIState

class ShoppingActivity : AppCompatActivity(), ShoppingContract.View {
    private val binding: ActivityShoppingBinding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    private val presenter: ShoppingContract.Presenter by lazy { createPresenter() }

    private val shoppingAdapter: ShoppingAdapter by lazy {
        ShoppingAdapter(
            shoppingEvent = makeCounterEvent()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolBar()
        initProductList()
        initLoadingButton()
        initUsers()
        initRecentlyViewedProducts()
    }

    override fun setRecentlyViewedProducts(recentlyViewedProducts: List<RecentlyViewedProductUIState>) {
        runOnUiThread {
            if (recentlyViewedProducts.isEmpty()) {
                setUIAboutRecentlyViewedProductIsVisible(false)
                return@runOnUiThread
            }
            setUIAboutRecentlyViewedProductIsVisible(true)

            binding.recyclerViewRecentlyViewed.adapter =
                RecentlyViewedProductListAdapter(recentlyViewedProducts) {
                    ProductDetailActivity.startActivity(this, it)
                }
        }
    }

    override fun addProducts(products: List<ProductUIState>) {
        runOnUiThread {
            shoppingAdapter.addItems(products)
        }
    }

    override fun changeProduct(product: ProductUIState) {
        runOnUiThread {
            shoppingAdapter.changeItem(product)
        }
    }

    override fun setProducts(products: List<ProductUIState>) {
        runOnUiThread {
            binding.layoutSkeletonProductList.isVisible = false
            binding.viewProductList.isVisible = true
            shoppingAdapter.setItems(products)
            binding.recyclerViewMainProduct.smoothScrollToPosition(0)
        }
    }

    override fun setCanLoadMore(canLoadMore: Boolean) {
        runOnUiThread {
            binding.btnLoading.isVisible = canLoadMore
        }
    }

    override fun setCartItemCount(count: Int) {
        runOnUiThread {
            binding.cartCount = count
        }
    }

    override fun showCart() {
        CartActivity.startActivity(this)
    }

    override fun showOrderList() {
        OrderListActivity.startActivity(this)
    }

    override fun showUserList(users: List<User>) {
        binding.shoppingUserSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            users.map { it.email }
        )
        binding.shoppingUserSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    presenter.selectUser(users[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbarProductList)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.ibCart.setOnClickListener {
            presenter.openCart()
        }
        binding.ibOrders.setOnClickListener {
            presenter.openOrderList()
        }
        binding.cartCount = 0
    }

    private fun initProductList() {
        binding.recyclerViewMainProduct.adapter = shoppingAdapter
    }

    private fun initLoadingButton() {
        binding.btnLoading.setOnClickListener {
            presenter.loadProductsNextPage()
        }
    }

    private fun initUsers() {
        presenter.loadUsers()
    }

    private fun initRecentlyViewedProducts() {
        presenter.loadRecentlyViewedProducts()
    }

    private fun createPresenter(): ShoppingPresenter {
        val productRemoteService = ProductRemoteService(ShoppingRetrofit.retrofit)
        val dbHelper = DbHelper.getDbInstance(this)
        val recentlyViewedProductMemoryDao = RecentlyViewedProductMemoryDao(dbHelper)
        val defaultRecentlyViewedProductRepository = DefaultRecentlyViewedProductRepository(
            recentlyViewedProductMemoryDao, productRemoteService
        )
        val defaultCartItemRepository = DefaultCartItemRepository(
            CartItemRemoteService(ShoppingRetrofit.retrofit)
        )
        val defaultProductRepository = DefaultProductRepository(productRemoteService)
        val userRepository = DefaultUserRepository(UserMemorySource(), UserRemoteSource())
        return ShoppingPresenter(
            this,
            defaultRecentlyViewedProductRepository,
            defaultProductRepository,
            defaultCartItemRepository,
            userRepository
        )
    }

    private fun setUIAboutRecentlyViewedProductIsVisible(isVisible: Boolean) {
        binding.tvRecentlyViewedProduct.isVisible = isVisible
        binding.recyclerViewRecentlyViewed.isVisible = isVisible
        binding.viewSeparatorRecyclerView.isVisible = isVisible
    }

    private fun makeCounterEvent() = object : ShoppingEvent {
        override fun onClick(productId: Long) {
            ProductDetailActivity.startActivity(this@ShoppingActivity, productId)
        }

        override fun onClickAddToCartButton(productId: Long) {
            presenter.addProductToCart(productId)
        }

        override fun onClickPlus(id: Long) {
            presenter.plusCount(id)
        }

        override fun onClickMinus(id: Long) {
            presenter.minusCount(id)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ShoppingActivity::class.java).apply {}
            context.startActivity(intent)
        }
    }
}
