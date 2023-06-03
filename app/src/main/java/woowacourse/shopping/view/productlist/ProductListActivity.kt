package woowacourse.shopping.view.productlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRemoteRepository
import woowacourse.shopping.data.repository.ProductRemoteRepository
import woowacourse.shopping.data.repository.RecentViewedDbRepository
import woowacourse.shopping.data.repository.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityProductListBinding
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.view.cart.CartActivity
import woowacourse.shopping.view.mypage.MypageActivity
import woowacourse.shopping.view.orderhistory.OrderHistoryActivity
import woowacourse.shopping.view.productdetail.ProductDetailActivity

class ProductListActivity : AppCompatActivity(), ProductListContract.View {
    private val binding: ActivityProductListBinding by lazy {
        ActivityProductListBinding.inflate(layoutInflater)
    }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val id = it.data?.getIntExtra(ID, -1)
            when (it.resultCode) {
                RESULT_VIEWED -> {
                    presenter.updateRecentViewed(id ?: -1)
                }
                RESULT_ADDED -> {
                    presenter.updateRecentViewed(id ?: -1)
                    presenter.fetchCartCount()
                    presenter.fetchProductCount(id ?: -1)
                    showToastAddInCart()
                }
                RESULT_VISIT_CART -> {
                    presenter.fetchCartCount()
                    presenter.fetchProductsCounts()
                }
            }
        }
    private lateinit var presenter: ProductListContract.Presenter
    private lateinit var cartCountInAppBar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setLoadingUi()
        setUpPresenter()
        setUpActionBar()
        presenter.fetchProducts()
    }

    private fun setLoadingUi() {
        val skeletonAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.skeleton_anim)
        binding.skeletonProducts.root.startAnimation(skeletonAnim)
    }

    override fun stopLoading() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.skeletonProducts.root.visibility = View.GONE
            binding.skeletonProducts.root.clearAnimation()
            binding.gridProducts.visibility = View.VISIBLE
        }, 1500L)
    }

    private fun setUpPresenter() {
        presenter =
            ProductListPresenter(
                this,
                ProductRemoteRepository(ServerPreferencesRepository(this), ::showErrorMessageToast),
                RecentViewedDbRepository(this, ServerPreferencesRepository(this)),
                CartRemoteRepository(ServerPreferencesRepository(this), ::showErrorMessageToast),
            )
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    override fun showProducts(items: List<ProductListViewItem>) {
        val gridLayoutManager = GridLayoutManagerWrapper(this, 2)
        gridLayoutManager.spanSizeLookup = GridLayoutManagerSpanSizeLookup(items)
        binding.gridProducts.layoutManager = gridLayoutManager
        binding.gridProducts.adapter = ProductListAdapter(
            items,
            object : ProductListAdapter.OnItemClick {
                override fun onProductClick(product: ProductModel) {
                    presenter.showProductDetail(product)
                }

                override fun onShowMoreClick() {
                    presenter.loadMoreProducts()
                }

                override fun onProductClickAddFirst(id: Int) {
                    presenter.insertCartProduct(id)
                    presenter.fetchCartCount()
                }

                override fun onProductUpdateCount(cartId: Int, productId: Int, count: Int) {
                    presenter.updateCartProductCount(cartId, productId, count)
                }
            },
        )
    }

    private fun showErrorMessageToast(message: String?) {
        if (message == null) {
            Toast.makeText(this, getString(R.string.notify_nothing_data), Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun notifyAddProducts(position: Int, size: Int) {
        binding.gridProducts.adapter?.notifyItemRangeInserted(position, size)
    }

    override fun notifyRecentViewedChanged() {
        binding.gridProducts.adapter?.notifyDataSetChanged()
    }

    override fun notifyDataChanged(position: Int) {
        binding.gridProducts.adapter?.notifyItemChanged(position)
    }

    override fun onClickProductDetail(product: ProductModel, lastViewedProduct: ProductModel?) {
        val intent =
            ProductDetailActivity.newIntent(binding.root.context, product)
        resultLauncher.launch(intent)
    }

    override fun showCartCount(count: Int) {
        if (count == 0) {
            cartCountInAppBar.visibility = View.GONE
            return
        }
        cartCountInAppBar.text = count.toString()
        cartCountInAppBar.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        val cartActionView = menu?.findItem(R.id.cart)?.actionView
        if (cartActionView != null) {
            cartCountInAppBar = cartActionView.findViewById(R.id.text_cart_count)
            presenter.fetchCartCount()

            val imageButton = cartActionView.findViewById<ImageButton>(R.id.btn_cart)
            imageButton?.setOnClickListener {
                val intent = CartActivity.newIntent(this)
                resultLauncher.launch(intent)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mypage -> startActivity(MypageActivity.newIntent(this))
            R.id.order_history -> startActivity(OrderHistoryActivity.newIntent(this))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showToastAddInCart() {
        Toast.makeText(this, ADD_IN_CART_MESSAGE, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val ADD_IN_CART_MESSAGE = "상품이 담겼습니다. 장바구니를 확인해주세요."

        const val RESULT_VIEWED = 200
        const val RESULT_ADDED = 300
        const val RESULT_VISIT_CART = 400
        const val ID = "id"

        fun newIntent(context: Context): Intent {
            return Intent(context, ProductListActivity::class.java)
        }
    }
}
