package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSource
import woowacourse.shopping.data.datasource.impl.ServerStorePreferenceDataSource
import woowacourse.shopping.data.repository.impl.CartRemoteRepository
import woowacourse.shopping.data.repository.impl.ServerPreferencesRepository
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.view.order.OrderActivity
import woowacourse.shopping.view.productlist.ProductListActivity

class CartActivity : AppCompatActivity(), CartContract.View {
    private val binding: ActivityCartBinding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val presenter: CartContract.Presenter by lazy {
        CartPresenter(
            this,
            CartRemoteRepository(
                CartRemoteDataSource(
                    ServerPreferencesRepository(ServerStorePreferenceDataSource(this)).getServerUrl(),
                ),
            ),
        )
    }
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpBinding()
        setLoadingUi()
        setContentView(binding.root)
        setUpActionBar()
        presenter.fetchProducts()
    }

    override fun finish() {
        setResult(ProductListActivity.RESULT_VISIT_CART, intent)
        super.finish()
    }

    private fun setLoadingUi() {
        val skeletonAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.skeleton_anim)
        binding.skeletonCarts.root.startAnimation(skeletonAnim)
    }

    override fun stopLoading() {
        runOnUiThread {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.skeletonCarts.root.visibility = View.GONE
                binding.skeletonCarts.root.clearAnimation()
                binding.recyclerCart.visibility = View.VISIBLE
            }, 1500L)
        }
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.presenter = presenter
    }

    private fun setUpActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showProducts(items: List<CartViewItem>) {
        adapter = CartAdapter(
            items.toMutableList(),
            object : CartAdapter.OnItemClick {
                override fun onRemoveClick(id: Int) {
                    presenter.removeProduct(id)
                }

                override fun onNextClick() {
                    presenter.fetchNextPage()
                }

                override fun onPrevClick() {
                    presenter.fetchPrevPage()
                }

                override fun onUpdateCount(id: Int, count: Int) {
                    presenter.updateCartProductCount(id, count)
                }

                override fun onSelectProduct(product: CartProductModel) {
                    presenter.checkProduct(product)
                }
            },
        )
        binding.recyclerCart.adapter = adapter
    }

    override fun showNotSuccessfulErrorToast() {
        Toast.makeText(this, getString(R.string.server_communication_error), Toast.LENGTH_LONG)
            .show()
    }

    override fun showServerFailureToast() {
        Toast.makeText(this, getString(R.string.server_not_response_error), Toast.LENGTH_LONG)
            .show()
    }

    override fun showServerResponseWrongToast() {
        Toast.makeText(this, getString(R.string.server_response_wrong), Toast.LENGTH_LONG).show()
    }

    override fun changeItems(newItems: List<CartViewItem>) {
        adapter.updateItems(newItems.toList())
    }

    override fun showOrderActivity(selectedCartProducts: List<CartProduct>) {
        startActivity(OrderActivity.newIntent(this, selectedCartProducts))
        finish()
    }

    override fun showProductsNothingToast() {
        Toast.makeText(this, R.string.products_nothing, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}