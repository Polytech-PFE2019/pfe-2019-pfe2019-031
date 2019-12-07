package ez.com.inside.activities.permissions;

import android.content.res.Resources;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Charly on 05/12/2017.
 */

public class IconItemDecoration extends RecyclerView.ItemDecoration
{
    private int spaceHorizontal;
    private int spaceVertical;

    public IconItemDecoration()
    {
        Resources r = Resources.getSystem();
        spaceHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        spaceVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.left = spaceHorizontal;
        outRect.right = spaceHorizontal;
        outRect.bottom = spaceVertical / 2;
        outRect.top = spaceVertical / 2;
    }
}
