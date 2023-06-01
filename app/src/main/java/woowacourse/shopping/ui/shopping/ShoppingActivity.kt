package woowacourse.shopping.ui.shopping

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.product.ProductDataSourceImpl
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSourceImpl
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.database.recentProduct.RecentProductDatabase
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.productdetail.ProductDetailActivity
import woowacourse.shopping.ui.shopping.contract.ShoppingContract
import woowacourse.shopping.ui.shopping.contract.presenter.ShoppingPresenter
import woowacourse.shopping.ui.shopping.viewHolder.ProductsOnClickListener

class ShoppingActivity :
    AppCompatActivity(),
    ShoppingContract.View,
    ProductsOnClickListener {
    private lateinit var binding: ActivityShoppingBinding
    private lateinit var presenter: ShoppingContract.Presenter

    private var cartSize: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shopping)
        setSupportActionBar(binding.toolbar)

        presenter = ShoppingPresenter(
            this,
            ProductRepositoryImpl(
                ProductDataSourceImpl(),
            ),
            RecentProductDatabase(this),
            CartRepositoryImpl(
                ShoppingCartDataSourceImpl(AuthInfoDataSourceImpl.getInstance(this)),
            ),
        )

        initLayoutManager()
        presenter.initProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        val actionView = menu?.findItem(R.id.cart)?.actionView
        actionView?.findViewById<ImageView>(R.id.cartBtn)?.setOnClickListener { navigateToCart() }
        actionView?.findViewById<TextView>(R.id.cartSize)?.let { cartSize = it }
        presenter.updateCountSize()
        return true
    }

    override fun onResume() {
        super.onResume()
        presenter.updateProducts()
        presenter.updateCountSize()
    }

    private fun initLayoutManager() {
        val layoutManager = GridLayoutManager(this@ShoppingActivity, 2)
        val spanCount = 2

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (binding.productRecyclerview.adapter?.getItemViewType(position)) {
                    ProductsItemType.TYPE_FOOTER -> spanCount
                    ProductsItemType.TYPE_ITEM -> 1
                    else -> spanCount
                }
            }
        }
        binding.productRecyclerview.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % spanCount + 1
                val space = 20

                /** 마지막 열(column-N)에 있는 아이템인 경우 우측에 [space] 만큼의 여백을 추가한다 */
                if (column == spanCount) {
                    outRect.right = space
                }
                /** 모든 아이템의 좌측과 하단에 [space] 만큼의 여백을 추가한다. */
                outRect.top = space
                outRect.left = space
                outRect.bottom = space
            }
        })
        binding.productRecyclerview.layoutManager = layoutManager
    }

    override fun setProducts(data: List<ProductsItemType>) {
        binding.productRecyclerview.adapter = ProductsAdapter(
            data,
            this,
            presenter::fetchMoreProducts,
        )

        binding.productRecyclerview.visibility = View.VISIBLE
        binding.includeShoppingSkeleton.root.visibility = View.GONE
    }

    override fun navigateToProductDetail(id: Long, latestProduct: ProductUIModel?) {
        startActivity(ProductDetailActivity.from(this, id, latestProduct))
    }

    override fun addProducts(data: List<ProductsItemType>) {
        binding.productRecyclerview.adapter?.let {
            if (it is ProductsAdapter) {
                it.updateData(data)
            }
        }
    }

    override fun showCountSize(size: Int) {
        cartSize?.text = size.toString()
    }

    override fun updateItem(id: Long, count: Int) {
        binding.productRecyclerview.adapter?.let {
            if (it is ProductsAdapter) {
                it.updateItemCount(id, count)
            }
        }
    }

    private fun navigateToCart() {
        startActivity(CartActivity.from(this))
    }

    override fun onClick(id: Long) {
        presenter.navigateToItemDetail(id)
    }

    override fun onAddCart(id: Long, count: Int) {
        presenter.insertItem(id, count)
    }

    override fun increaseCount(id: Long) {
        presenter.increaseCount(id)
    }

    override fun decreaseCount(id: Long) {
        presenter.decreaseCount(id)
    }
}
