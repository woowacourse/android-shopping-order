package woowacourse.shopping.view.productlist

// import woowacourse.shopping.view.cart.CartActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRemoteRepository
import woowacourse.shopping.data.repository.ProductRemoteRepository
import woowacourse.shopping.data.repository.RecentViewedDbRepository
import woowacourse.shopping.databinding.ActivityProductListBinding
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.view.cart.CartActivity
import woowacourse.shopping.view.productdetail.ProductDetailActivity

class ProductListActivity : AppCompatActivity(), ProductListContract.View {
    private lateinit var binding: ActivityProductListBinding
    private lateinit var presenter: ProductListContract.Presenter
    private lateinit var cartCountInAppBar: TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setContentView(binding.root)
        setLoadingUi()
        setUpPresenter()
        setUpActionBar()
//        presenter.fetchProducts()
    }

    private fun setLoadingUi() {
        val skeletonAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.skeleton_anim)
        binding.skeletonProducts.root.startAnimation(skeletonAnim)
    }

    override fun stopLoading() {
        runOnUiThread {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.skeletonProducts.root.visibility = View.GONE
                binding.skeletonProducts.root.clearAnimation()
                binding.gridProducts.visibility = View.VISIBLE
            }, 1500L)
        }
    }

    private fun setUpBinding() {
        binding = ActivityProductListBinding.inflate(layoutInflater)
    }

    private fun setUpPresenter() {
        val productRemoteRepository = ProductRemoteRepository("http://43.200.181.131:8080")
        presenter =
            ProductListPresenter(
                this,
                productRemoteRepository,
                RecentViewedDbRepository(this, productRemoteRepository),
                CartRemoteRepository("http://43.200.181.131:8080"),
            )
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    override fun showProducts(items: List<ProductListViewItem>) {
        runOnUiThread {
            val gridLayoutManager = GridLayoutManagerWrapper(this, 2)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val isHeader = items[position].type == ProductListViewType.RECENT_VIEWED_ITEM
                    val isFooter = items[position].type == ProductListViewType.SHOW_MORE_ITEM
                    return if (isHeader || isFooter) {
                        HEADER_FOOTER_SPAN
                    } else {
                        PRODUCT_ITEM_SPAN
                    }
                }
            }
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
    }

    override fun notifyAddProducts(position: Int, size: Int) {
        runOnUiThread {
            binding.gridProducts.adapter?.notifyItemRangeInserted(position, size)
        }
    }

    override fun notifyRecentViewedChanged() {
        runOnUiThread {
            binding.gridProducts.adapter?.notifyItemChanged(0)
        }
    }

    override fun notifyDataChanged(position: Int) {
        runOnUiThread { binding.gridProducts.adapter?.notifyItemChanged(position) }
    }

    override fun onClickProductDetail(product: ProductModel, lastViewedProduct: ProductModel?) {
        val intent =
            ProductDetailActivity.newIntent(binding.root.context, product, lastViewedProduct)
        resultLauncher.launch(intent)
    }

    override fun showCartCount(count: Int) {
        runOnUiThread {
            if (count == 0) {
                cartCountInAppBar.visibility = View.GONE
                return@runOnUiThread
            }
            cartCountInAppBar.text = count.toString()
            cartCountInAppBar.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        val itemActionView = menu?.findItem(R.id.cart)?.actionView
        if (itemActionView != null) {
            cartCountInAppBar = itemActionView.findViewById(R.id.text_cart_count)
            presenter.fetchCartCount()

            val imageButton = itemActionView.findViewById<ImageButton>(R.id.btn_cart)
            imageButton?.setOnClickListener {
                val intent = CartActivity.newIntent(this)
                resultLauncher.launch(intent)
            }
        }
        return true
    }

    override fun showToastAddInCart() {
        Toast.makeText(this, ADD_IN_CART_MESSAGE, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val ADD_IN_CART_MESSAGE = "상품이 담겼습니다. 장바구니를 확인해주세요."
        private const val HEADER_FOOTER_SPAN = 2
        private const val PRODUCT_ITEM_SPAN = 1
        const val RESULT_VIEWED = 200
        const val RESULT_ADDED = 300
        const val RESULT_VISIT_CART = 400
        const val ID = "id"

        fun newIntent(context: Context): Intent {
            return Intent(context, ProductListActivity::class.java)
        }
    }
}
