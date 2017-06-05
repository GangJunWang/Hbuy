package au.com.hbuy.aotong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.com.hbuy.aotong.utils.AppUtils;
import au.com.hbuy.aotong.view.fragment.OtherFragment;
import au.com.hbuy.aotong.view.fragment.RepoFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by yangwei on 2016/7/27--11:35.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class GetAddressRepoActivity extends BaseFragmentActivity {

    @Bind(R.id.verticalviewpager)
    VerticalViewPager verticalViewPager;
    @Bind(R.id.bt_enter)
    Button btEnter;
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;
    private FragmentActivity mContext;
    public final static int GOSERVICE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_get_repo);
        mContext = this;
        ButterKnife.bind(this);
        //  verticalViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pagemargin));
        //  verticalViewPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));

        initData();
    }

    private void initData() {
        List<Fragment> list = new ArrayList<>();
        RepoFragment repoFragment = RepoFragment.newInstance();
        repoFragment.setActivity(mContext);
        list.add(repoFragment);
        OtherFragment otherFragment = OtherFragment.newInstance();
        otherFragment.setActivity(mContext);
        list.add(otherFragment);

        verticalViewPager.setAdapter(new RepoHintAdapter(this.getSupportFragmentManager(), list));
        verticalViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationY(vertMargin - horzMargin / 2);
                    } else {
                        view.setTranslationY(-vertMargin + horzMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });
    }

    @OnClick(R.id.bt_enter)
    public void onClick() {
        AppUtils.showActivity(this, MainActivity.class);
        finish();
    }

    public class RepoHintAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragment;

        public RepoHintAdapter(FragmentManager fm) {
            super(fm);
        }

        public RepoHintAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.listFragment = list;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.d(resultCode + "--requestCode=" + requestCode);
        if (requestCode == GOSERVICE) {
            AppUtils.showActivity(this, MainActivity.class);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                AppUtils.showActivity(this, MainActivity.class);
                finish();
                break;
        }
        return false;
    }
}
