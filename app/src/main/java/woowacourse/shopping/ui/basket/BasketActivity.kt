package woowacourse.shopping.ui.basket

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.basket.BasketRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.user.UserRemoteDataSourceImpl
import woowacourse.shopping.data.repository.BasketRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.UserRepositoryImpl
import woowacourse.shopping.databinding.ActivityBasketBinding
import woowacourse.shopping.databinding.DialogUsingPointBinding
import woowacourse.shopping.ui.basket.skeleton.SkeletonBasketProductAdapter
import woowacourse.shopping.ui.model.UiBasketProduct
import woowacourse.shopping.ui.model.User
import woowacourse.shopping.ui.orderfinish.OrderDetailActivity
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.turnOffSupportChangeAnimation

class BasketActivity : AppCompatActivity(), BasketContract.View {
    private lateinit var presenter: BasketContract.Presenter
    private lateinit var binding: ActivityBasketBinding
    private lateinit var basketAdapter: BasketAdapter
    private lateinit var dialogBinding: DialogUsingPointBinding
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSetResult()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_basket)
        binding.rvBasket.turnOffSupportChangeAnimation()
        initSkeletonAdapter()
        initPresenter()
        initAdapter()
        initToolbarBackButton()
        navigatorClickListener()
        initTotalCheckBoxOnCheckedChangedListener()
        initOrderButtonListener()
    }

    private fun initSkeletonAdapter() {
        binding.skeletonBasketProduct.rvSkeletonBasketProduct.adapter =
            SkeletonBasketProductAdapter()
    }

    private fun initTotalCheckBoxOnCheckedChangedListener() {
        binding.checkButtonClickListener =
            { presenter.fetchTotalCheckToCurrentPage(binding.cbTotal.isChecked) }
    }

    private fun initSetResult() {
        setResult(Activity.RESULT_OK, ShoppingActivity.getResultIntent())
    }

    private fun initPresenter() {
        presenter = BasketPresenter(
            this,
            basketRepository = BasketRepositoryImpl(BasketRemoteDataSourceImpl()),
            userRepository = UserRepositoryImpl(UserRemoteDataSourceImpl()),
            orderRepository = OrderRepositoryImpl(OrderRemoteDataSourceImpl()),
        )
    }

    private fun initAdapter() {
        basketAdapter = BasketAdapter(
            presenter::deleteBasketProduct,
            presenter::minusBasketProductCount,
            presenter::plusBasketProductCount,
            presenter::updateBasketProductCheckState
        )
        binding.rvBasket.adapter = basketAdapter
    }

    private fun initToolbarBackButton() {
        binding.tbBasket.setNavigationOnClickListener {
            finish()
        }
    }

    private fun navigatorClickListener() {
        binding.btnPrevious.setOnClickListener {
            presenter.updatePreviousPage()
        }
        binding.btnNext.setOnClickListener {
            presenter.updateNextPage()
        }
    }

    override fun updateBasketProducts(basketProducts: List<UiBasketProduct>) {
        runOnUiThread {
            basketAdapter.submitList(basketProducts)
        }
    }

    override fun updateNavigatorEnabled(previous: Boolean, next: Boolean) {
        binding.btnPrevious.isEnabled = previous
        binding.btnNext.isEnabled = next
    }

    override fun updateCurrentPage(currentPage: Int) {
        binding.tvCurrentPage.text = currentPage.toString()
    }

    override fun updateTotalPrice(totalPrice: Int) {
        binding.totalPrice = totalPrice
    }

    override fun updateCheckedProductsCount(checkedProductsCount: Int) {
        binding.checkedCount = checkedProductsCount
    }

    override fun updateTotalCheckBox(isChecked: Boolean) {
        binding.cbTotal.isChecked = isChecked
    }

    override fun updateSkeletonState(isLoaded: Boolean) {
        binding.isLoaded = isLoaded
    }

    override fun showUsingPointDialog(user: User) {
        initUsingPointDialog(user)
        dialog.show()
    }

    override fun navigateToOrderDetail(orderId: Int) {
        val intent = OrderDetailActivity.getIntent(
            context = this,
            orderId = 1
        )

        startActivity(intent)
    }

    private fun initOrderButtonListener() {
        binding.tvOrder.setOnClickListener {
            presenter.usePoint()
        }
    }

    private fun initUsingPointDialog(user: User) {
        dialogBinding = DialogUsingPointBinding.inflate(layoutInflater).apply {
            this.user = user
            btnDialogOrder.setOnClickListener {
                val usingPoint = tvUsingPoint.text
                    .toString()
                    .toInt()

                presenter.addOrder(usingPoint)
            }
            tvUsingPoint.addTextChangedListener {
                if (!it.isNullOrBlank()) {
                    val usingPoint = it.toString().toInt()

                    btnDialogOrder.isEnabled = user.point >= usingPoint
                }
            }
        }

        dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, BasketActivity::class.java)
    }
}
