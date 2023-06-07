package woowacourse.shopping.ui.basket

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.basket.BasketRemoteDataSourceImpl
import woowacourse.shopping.data.repository.BasketRepositoryImpl
import woowacourse.shopping.databinding.ActivityBasketBinding
import woowacourse.shopping.ui.basket.skeleton.SkeletonBasketProductAdapter
import woowacourse.shopping.ui.model.BasketProductUiModel
import woowacourse.shopping.ui.payment.PaymentActivity
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.turnOffSupportChangeAnimation

class BasketActivity : AppCompatActivity(), BasketContract.View {
    private lateinit var presenter: BasketContract.Presenter
    private lateinit var binding: ActivityBasketBinding
    private lateinit var basketAdapter: BasketAdapter

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
            view = this,
            basketRepository = BasketRepositoryImpl(
                basketRemoteDataSource = BasketRemoteDataSourceImpl()
            ),
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

    override fun updateBasketProducts(basketProducts: List<BasketProductUiModel>) {
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

    override fun updateOrderButtonState(isAvailable: Boolean) {
        binding.btnOrder.isEnabled = isAvailable
    }

    override fun showPaymentView(
        basketProducts: List<BasketProductUiModel>,
        totalPrice: Int,
    ) {
        val intent = PaymentActivity.getIntent(
            context = this,
            totalPrice = totalPrice,
            basketProducts = basketProducts.toTypedArray()
        )

        startActivity(intent)
    }

    override fun showErrorMessage(errorMessage: String) {
        val intent = ShoppingActivity.getIntent(this)

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    private fun initOrderButtonListener() {
        binding.btnOrder.setOnClickListener {
            presenter.startPayment()
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, BasketActivity::class.java)
    }
}
