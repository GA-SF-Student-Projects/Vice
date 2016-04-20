package martell.com.vice;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import martell.com.vice.adapters.ViewPagerAdapter;
import martell.com.vice.models.Article;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Main";

    private static final String CATEGORY_TITLE_KEY = "Title";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private ArrayList<Article> articles;
    private RecyclerView articleRV;
    public ViceAPIService viceService;
    private ArticleAdapter articleAdapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articles = new ArrayList<>();
        retrofit = new Retrofit.Builder().baseUrl("http://www.vice.com/en_us/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        viceService = retrofit.create(ViceAPIService.class);
        //displayLatestArticles();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //setupViewPagerOneFragment(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.d(TAG, "onTabSelected: " + adapter.getItem(tab.getPosition()));
//                String tag = adapter.getItem(tab.getPosition()).getTag();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                //transaction.add(adapter.getItem(tab.getPosition()), tag);
//                transaction.replace(R.id.viewpager, adapter.getItem(tab.getPosition()));
//                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //viewPager.setCurrentItem(1);

    }

//    private void setupViewPagerOneFragment(final ViewPager viewPager) {
//        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//
//        CategoryFragment home = new CategoryFragment();
//        Bundle bundleHome = new Bundle();
//        bundleHome.putString(CATEGORY_TITLE_KEY, "Home");
//        home.setArguments(bundleHome);
//
//        CategoryFragment news = new CategoryFragment();
//        Bundle bundleNews = new Bundle();
//        bundleNews.putString(CATEGORY_TITLE_KEY, "News");
//        news.setArguments(bundleNews);
//
//        CategoryFragment music = new CategoryFragment();
//        Bundle bundleMusic = new Bundle();
//        bundleMusic.putString(CATEGORY_TITLE_KEY, "Music");
//        music.setArguments(bundleMusic);
//
//        CategoryFragment music = new CategoryFragment("Music");
//        CategoryFragment sports = new CategoryFragment("Sports");
//        CategoryFragment tech = new CategoryFragment("Tech");
//        CategoryFragment travel = new CategoryFragment("Travel");
//        CategoryFragment fashion = new CategoryFragment("Fashion");
//        CategoryFragment guide = new CategoryFragment("Guide");
//
//        adapter.addFragment(home, bundleHome.getString(CATEGORY_TITLE_KEY));
//        adapter.addFragment(news, bundleNews.getString(CATEGORY_TITLE_KEY));
//        adapter.addFragment(music, bundleMusic.getString(CATEGORY_TITLE_KEY));
//        adapter.addFragment(sports, sports.getTitle());
//        adapter.addFragment(tech, tech.getTitle());
//        adapter.addFragment(travel, travel.getTitle());
//        adapter.addFragment(fashion, fashion.getTitle());
//        adapter.addFragment(guide, guide.getTitle());
//
//        adapter.notifyDataSetChanged();
//
//        viewPager.setAdapter(adapter);
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }

}
