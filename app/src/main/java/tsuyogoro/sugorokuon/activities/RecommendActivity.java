package tsuyogoro.sugorokuon.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.RecommendActivityLayoutBinding;
import tsuyogoro.sugorokuon.fragments.timetable.RecommendFragment;

public class RecommendActivity extends DrawableActivity {

    private RecommendActivityLayoutBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.recommend_activity_layout, getContentRoot(), true);

        mBinding.recommendActivityToolbar.setTitle(R.string.title_recommends);
        setSupportActionBar(mBinding.recommendActivityToolbar);

        setupDrawer(false);

        RecommendFragment fragment = new RecommendFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.recommend_activity_fragment_area, fragment);

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
