package woowacourse.shopping.feature.goods

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.goods.repository.GoodsLocalDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRemoteDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRepositoryImpl
import woowacourse.shopping.databinding.ActivityGoodsBinding
import woowacourse.shopping.databinding.MenuCartNavbarBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.feature.BaseActivity
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.goods.adapter.horizontal.HorizontalSectionAdapter
import woowacourse.shopping.feature.goods.adapter.horizontal.RecentlyViewedGoodsAdapter
import woowacourse.shopping.feature.goods.adapter.vertical.GoodsAdapter
import woowacourse.shopping.feature.goods.adapter.vertical.GoodsSkeletonAdapter
import woowacourse.shopping.feature.goods.adapter.vertical.MoreButtonAdapter
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsActivity
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsActivity.Companion.CART_KEY
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsActivity.Companion.EXTRA_SOURCE
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsActivity.Companion.SOURCE_GOODS_LIST
import woowacourse.shopping.feature.login.LoginActivity
import woowacourse.shopping.feature.toUiModel
import woowacourse.shopping.util.toUi

class GoodsActivity : BaseActivity<ActivityGoodsBinding>() {

    private val viewModel: GoodsViewModel by viewModels {
        GoodsViewModelFactory(
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this))
            )
        )
    }

    override fun inflateBinding(): ActivityGoodsBinding = ActivityGoodsBinding.inflate(layoutInflater)

    private val recentlyViewedGoodsAdapter by lazy {
        RecentlyViewedGoodsAdapter(this) { goods -> navigateGoodsDetails(goods) }
    }

    private val horizontalSelectionAdapter by lazy {
        HorizontalSectionAdapter(this, viewModel, recentlyViewedGoodsAdapter)
    }

    private val goodsAdapter by lazy {
        GoodsAdapter(
            goodsClickListener = { goods -> navigateGoodsDetails(goods) },
            quantityChangeListener = object : QuantityChangeListener {
                override fun onIncrease(cartItem: CartItem) {
                    viewModel.addCartItemOrIncreaseQuantity(cartItem)
                }

                override fun onDecrease(cartItem: CartItem) {
                    viewModel.removeCartItemOrDecreaseQuantity(cartItem.copy(quantity = 1))
                }
            }
        )
    }

    private val goodsSkeletonAdapter by lazy { GoodsSkeletonAdapter() }

    private val moreButtonAdapter by lazy {
        MoreButtonAdapter {
            viewModel.addPage()
        }
    }

    private val concatAdapter by lazy {
        ConcatAdapter(
            horizontalSelectionAdapter,
            goodsSkeletonAdapter,
            moreButtonAdapter
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvGoodsItems.adapter = concatAdapter
        binding.rvGoodsItems.layoutManager = getLayoutManager()
        binding.rvGoodsItems.addItemDecoration(
            GoodsGridItemDecoration(concatAdapter, GRID_GOODS_ITEM_HORIZONTAL_PADDING)
        )

        observeUiEvent()

        viewModel.recentlyViewedGoods.observe(this) { goods ->
            recentlyViewedGoodsAdapter.setItems(goods)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) {
                concatAdapter.removeAdapter(goodsSkeletonAdapter)
                concatAdapter.addAdapter(1, goodsAdapter)
            } else {
                if (!concatAdapter.adapters.contains(goodsSkeletonAdapter)) {
                    concatAdapter.addAdapter(1, goodsSkeletonAdapter)
                }
            }
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this) { event ->
            when (event) {
                is GoodsUiEvent.ShowToast -> {
                    val message = when (event.messageKey) {
                        ToastMessageKey.FAIL_LOGIN -> getString(R.string.toast_fail_login)
                        ToastMessageKey.FAIL_CART_LOAD -> getString(R.string.toast_fail_cart_load)
                        ToastMessageKey.FAIL_CART_ADD -> getString(R.string.toast_fail_cart_add)
                        ToastMessageKey.FAIL_CART_UPDATE -> getString(R.string.toast_fail_cart_update)
                        ToastMessageKey.FAIL_CART_DELETE -> getString(R.string.toast_fail_cart_delete)
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }

                is GoodsUiEvent.NavigateToLogin -> startActivity(Intent(this, LoginActivity::class.java))
                is GoodsUiEvent.NavigateToCart -> startActivity(CartActivity.newIntent(this))
            }
        }
    }

    private fun getLayoutManager(): GridLayoutManager {
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val (adapter, _) = concatAdapter.getWrappedAdapterAndPosition(position)
                return when (adapter) {
                    is GoodsAdapter, is GoodsSkeletonAdapter -> 1
                    else -> 2
                }
            }
        }
        return layoutManager
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)
        sharedPreferences.getString("basicKey", "")?.let { viewModel.login(it) }
        viewModel.fetchAndSetCartCache()
        viewModel.updateRecentlyViewedGoods()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_cart, menu)
        val menuItem = menu?.findItem(R.id.nav_cart)
        val navbarBinding = MenuCartNavbarBinding.inflate(layoutInflater)
        navbarBinding.lifecycleOwner = this
        navbarBinding.viewModel = viewModel
        menuItem?.actionView = navbarBinding.root
        return super.onCreateOptionsMenu(menu)
    }

    private fun navigateGoodsDetails(goods: Goods) {
        val intent = GoodsDetailsActivity.newIntent(this, goods.toUi())
        intent.putExtra(EXTRA_SOURCE, SOURCE_GOODS_LIST)
        intent.putExtra(CART_KEY, viewModel.findCart(goods)?.toUiModel())
        startActivity(intent)
    }

    companion object {
        private const val GRID_GOODS_ITEM_HORIZONTAL_PADDING = 14
    }
}
