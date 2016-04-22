package martell.com.vice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import martell.com.vice.adapters.ViewPagerAdapter;
import martell.com.vice.dbHelper.DatabaseHelper;
import martell.com.vice.fragment.LatestNewFragment;
import martell.com.vice.fragment.NavigationDrawerFragment;
import martell.com.vice.models.Article;
import martell.com.vice.services.ViceAPIService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NotificationPreferences{
    private static final String TAG = "Main";
    public static final String KEY_FRAGMENT_TITLE = "FragmentTitle";
    public static final String KEY_SHARED_PREF_NOTIF = "sharedPrefNotification";
    private ViewPager viewPager;
    private ArrayList<Article> articles;
    public ViceAPIService viceService;
    private Retrofit retrofit;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private String notificationPreferences;
    String latestArticleTitle;
    String latestArticleId;

    // Content provider authority
    public static final String AUTHORITY = "martell.com.vice.sync_adapter.StubProvider";
    // Account type
    public static final String ACCOUNT_TYPE = "example.com";
    // Account
    public static final String ACCOUNT = "default_account";
    Account mAccount;
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        SharedPreferences sharedPreferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        String notificationFromSharedPref = sharedPreferences.getString(KEY_SHARED_PREF_NOTIF,"");
        setNavigationDrawer(createBoolArrayList(notificationFromSharedPref));

        mAccount = createSyncAccount(this);

        articles = new ArrayList<>();
        retrofit = new Retrofit.Builder().baseUrl("http://www.vice.com/en_us/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        viceService = retrofit.create(ViceAPIService.class);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        setupViewPagerOneFragment(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        // Get the content resolver for your app
        mResolver = getContentResolver();

//        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
//        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, 30);



        setNotificationAlarmManager();

    }

    private void setupViewPagerOneFragment(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        LatestNewFragment home = new LatestNewFragment();
        Bundle bundleHome = new Bundle();
        bundleHome.putString(KEY_FRAGMENT_TITLE, "Home");
        home.setArguments(bundleHome);
        adapter.addFragment(home, "Home");

        LatestNewFragment news = new LatestNewFragment();
        Bundle bundleNews = new Bundle();
        bundleNews.putString(KEY_FRAGMENT_TITLE, "News");
        news.setArguments(bundleNews);
        adapter.addFragment(news, "News");

        LatestNewFragment music = new LatestNewFragment();
        Bundle bundleMusic = new Bundle();
        bundleMusic.putString(KEY_FRAGMENT_TITLE, "Music");
        music.setArguments(bundleMusic);
        adapter.addFragment(music, "Music");

        LatestNewFragment sports = new LatestNewFragment();
        Bundle bundleSports = new Bundle();
        bundleSports.putString(KEY_FRAGMENT_TITLE, "Sports");
        sports.setArguments(bundleSports);
        adapter.addFragment(sports, "Sports");

        LatestNewFragment tech = new LatestNewFragment();
        Bundle bundleTech = new Bundle();
        bundleTech.putString(KEY_FRAGMENT_TITLE, "Tech");
        tech.setArguments(bundleTech);
        adapter.addFragment(tech, "Tech");

        LatestNewFragment travel = new LatestNewFragment();
        Bundle bundleTravel = new Bundle();
        bundleTravel.putString(KEY_FRAGMENT_TITLE, "Travel");
        travel.setArguments(bundleTravel);
        adapter.addFragment(travel, "Travel");

        LatestNewFragment fashion = new LatestNewFragment();
        Bundle bundleFashion = new Bundle();
        bundleFashion.putString(KEY_FRAGMENT_TITLE, "Fashion");
        fashion.setArguments(bundleFashion);
        adapter.addFragment(fashion, "Fashion");

        LatestNewFragment guide = new LatestNewFragment();
        Bundle bundleGuide = new Bundle();
        bundleGuide.putString(KEY_FRAGMENT_TITLE, "Guide");
        guide.setArguments(bundleGuide);
        adapter.addFragment(guide, "Guide");

        LatestNewFragment bookmarks = new LatestNewFragment();
        Bundle bundleBookmarks = new Bundle();
        bundleBookmarks.putString(KEY_FRAGMENT_TITLE, "Bookmarks");
        //Need to change this when getNewsArticles is complete
        //from bundleNews to bundleBookmarks
        bookmarks.setArguments(bundleBookmarks);
        adapter.addFragment(bookmarks, "Bookmarks");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
          /*
           * If you don't set android:syncable="true" in
           * in your <provider> element in the manifest,
           * then call context.setIsSyncable(account, AUTHORITY, 1)
           * here.
           */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private ArrayList<Boolean> createBoolArrayList(String notificationPreferences){
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayList<Boolean> isCheckedArray = new ArrayList<>();
        String[] arrayNotificationPref = notificationPreferences.split(",");

        for (int i = 0; i < categories.length; i++){
            isCheckedArray.add(false);
            for (String curNotification: arrayNotificationPref) {
                if (categories[i].equals(curNotification)){
                    isCheckedArray.set(i,true);
                }
            }
        }

        return isCheckedArray;
    }

    private void setNavigationDrawer(ArrayList<Boolean> isCheckedArray) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.d(TAG, "SUPPORT ACTION BAR IS NULL");
        }
        String[] tabResourceArray = getResources().getStringArray(R.array.categories);
        List<NavDrawerEntry> drawerEntries = new ArrayList<>();

            drawerEntries.add(new NavDrawerItem(tabResourceArray[0]));
            drawerEntries.add(new NavDrawerDivider());
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[2]));
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[3]));
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[4]));
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[5]));
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[6]));
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[7]));
            drawerEntries.add(new NavDrawerToggle(tabResourceArray[8]));

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.initDrawer((android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout_main),
                toolbar, drawerEntries,isCheckedArray);
        Log.d(TAG, "THE initDrawer HAS BEEN CALLED ON MAIN");
    }

    @Override
    public void setNotificationPreferences(String notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
        Log.i(TAG, "setNotificationPreferences: " + notificationPreferences);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SHARED_PREF_NOTIF,notificationPreferences);
        editor.commit();
        super.onDestroy();
    }

    /** method below takes in latest articles and user preferences to generate notifications with
     * new articles related to user's favorite news categories
     */


    // this method needs to be given an article for notificationManager to push notification with
    // article data

    public void setNotificationAlarmManager() {
        Log.i(TAG, "onCreate: setAlarm was called");

        Long alertTime = new GregorianCalendar().getTimeInMillis()+5000;

        //perform sync here?
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, 30);

        Intent alertIntent = new Intent(this, NotificationPublisher.class);

        DatabaseHelper searchHelper = DatabaseHelper.getInstance(this);
//        searchHelper.findArticles();
//        Cursor cursor = searchHelper.getLatestArticle();
//        latestArticleId = cursor.getString(0);
        latestArticleTitle = searchHelper.getLatestArticleTitle(0);
        Log.i(TAG, "latestArticleId: " + latestArticleId);
        Log.i(TAG, "latestArticleTitle: " + latestArticleTitle);

        alertIntent.putExtra("TITLE_KEY", latestArticleTitle);
        alertIntent.putExtra("ID_KEY", latestArticleId);

        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);
        tStackBuilder.addParentStack(MainActivity.class);
        tStackBuilder.addNextIntent(alertIntent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        Log.i(TAG, "setAlarm: alarm manager should have been set");
    }

}
