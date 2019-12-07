package ez.com.inside.activities.permissions;

import android.content.res.Resources;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Charly on 05/12/2017.
 */

public class ItemDecoration extends RecyclerView.ItemDecoration
{
    private int spaceHorizontal;
    private int spaceVertical;

    public ItemDecoration()
    {
        Resources r = Resources.getSystem();
        spaceHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
        spaceVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());
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
