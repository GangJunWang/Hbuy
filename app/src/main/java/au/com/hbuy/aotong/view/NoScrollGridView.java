package au.com.hbuy.aotong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.socks.library.KLog;

public class NoScrollGridView extends GridView {
	public boolean isOnMeasure;


	public NoScrollGridView(Context context) {
		super(context);
	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		isOnMeasure = true;
		KLog.d();
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		KLog.d();
		isOnMeasure = false;
		super.onLayout(changed, l, t, r, b);
	}
}
