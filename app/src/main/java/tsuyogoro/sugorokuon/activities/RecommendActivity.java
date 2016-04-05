package tsuyogoro.sugorokuon.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.RecommendActivityLayoutBinding;
import tsuyogoro.sugorokuon.databinding.SearchActivityLayoutBinding;
import tsuyogoro.sugorokuon.fragments.timetable.RecommendFragment;
import tsuyogoro.sugorokuon.fragments.timetable.SearchFragment;

public class RecommendActivity extends AppCompatActivity {

    private RecommendActivityLayoutBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.recommend_activity_layout);

        mBinding.recommendActivityToolbar.setTitle(R.string.title_recommends);
        setSupportActionBar(mBinding.recommendActivityToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
