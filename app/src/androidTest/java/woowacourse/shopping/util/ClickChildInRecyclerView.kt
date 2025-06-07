package woowacourse.shopping.util

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId

fun clickChildInRecyclerView(
    recyclerViewId: Int,
    position: Int,
    childViewId: Int,
) {
    onView(withId(recyclerViewId))
        .perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                clickOnViewChild(childViewId),
            ),
        )
}
