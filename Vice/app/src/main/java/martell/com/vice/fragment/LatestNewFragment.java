package martell.com.vice.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import martell.com.vice.ArticleActivity;
import martell.com.vice.ArticleAdapter;
import martell.com.vice.BookmarksHelper;
import martell.com.vice.MainActivity;
import martell.com.vice.R;
import martell.com.vice.RV_SpaceDecoration;
import martell.com.vice.dbHelper.DatabaseHelper;
import martell.com.vice.models.Article;
import martell.com.vice.models.ArticleArray;
import martell.com.vice.services.ViceAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HEAD;

/**
 * Created by adao1 on 4/19/2016.
 */

public class LatestNewFragment extends Fragment implements ArticleAdapter.OnRVItemClickListener, ArticleAdapter.OnLastArticleShownListener, BookmarksHelper.BookmarksResponse {

    private static final String TAG = "Latest News Fragment";
    private ArrayList<String> tabViewsTitle;
    private ArrayList<Article> articles;
    private RecyclerView articleRV;
    public ViceAPIService viceService;
    private ArticleAdapter articleAdapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private Retrofit retrofit;
    private String fragTitle;
    boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private GridLayoutManager gridLayoutManager;
    private TextView tabTitleView;
    private String notificationPreferences;

    SharedPreferences sharedPreferences;
    String stringSharedPrefs;
    String[] arrayNotificationPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragTitle = getArguments().getString(MainActivity.KEY_FRAGMENT_TITLE);
        Log.d(TAG,"onCreate has been called and " +fragTitle);
        
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest_news,container,false);
        articleRV = (RecyclerView)view.findViewById(R.id.articleRV);
        tabTitleView = (TextView) view.findViewById(R.id.main_title);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articles = new ArrayList<>();
        retrofit = new Retrofit.Builder().baseUrl("http://www.vice.com/en_us/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        viceService = retrofit.create(ViceAPIService.class);
        displayLatestArticles(0);
        makeRV();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibilityHint has been run " + fragTitle);
        if (fragTitle != null) {
            if (isVisibleToUser && fragTitle.equals("Bookmarks")) {
                    Log.d(TAG, "!!!!!IS VISIBLE AND IS BOOKMARKS!!!! " + fragTitle);
                    displayLatestArticles(0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint() && fragTitle.equals("Bookmarks")){
            Log.d(TAG,"ON RESUME HAS BEEN CALLED AND FRAG TITLE = BOOKMARKS");
            displayLatestArticles(0);
        }

    }

    private void displayLatestArticles(int numPages){
        Call<ArticleArray> call =null;
        fragTitle = getArguments().getString(MainActivity.KEY_FRAGMENT_TITLE);

        if (tabTitleView != null)
            tabTitleView.setText(fragTitle);

        Log.d(TAG, "THIS IS THE FRAGMENT TITLE " + fragTitle);

        if (fragTitle.equals("Home")) {
            call = viceService.latestArticles(numPages);

        } else if(fragTitle.equals("Bookmarks")) {
            Log.d(TAG, "BOOKSMARKS HAS BEEN SELECTED IN DISPLAYLATEST ARTICLES");
            DatabaseHelper bookmarkDatabaseHelper = DatabaseHelper.getInstance(getActivity());
            BookmarksHelper bookmarksHelper = new BookmarksHelper(this, bookmarkDatabaseHelper);
            bookmarksHelper.execute();

        } else {
            Log.d(TAG, "ELSE IS CALLED IN DISPLAYLATEST ARTICLES + CURTITLE " + fragTitle);
            call = viceService.getArticlesByCategory(fragTitle,numPages);

        }

        if (!fragTitle.equals("Bookmarks")) {
            if (call != null) {
                call.enqueue(new Callback<ArticleArray>() {
                    @Override
                    public void onResponse(Call<ArticleArray> call, Response<ArticleArray> response) {
                        Article[] articleArray = response.body().getData().getItems();
                        ArrayList<Article> articlesNew = new ArrayList<>(Arrays.asList(articleArray));
                        articles.addAll(articlesNew);


                        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                        stringSharedPrefs = sharedPreferences.getString(MainActivity.KEY_SHARED_PREF_NOTIF, "");
                        arrayNotificationPref = stringSharedPrefs.split(",");
                        Log.i(TAG, "onResponse: shared prefs = " + sharedPreferences);
                        Log.i(TAG, "onResponse: title = " + fragTitle);
                        Log.i(TAG, "onResponse: prefs as string" + stringSharedPrefs);

                        if (Arrays.asList(arrayNotificationPref).contains(fragTitle)) {
                            // if a notification pref is on, add those articles to the database here
                            // will use them as a reference point for notifications on new articles
                            Log.i(TAG, "onResponse: entered if statement for database entry");
                            DatabaseHelper searchHelper = DatabaseHelper.getInstance(getActivity());
                            // checks if the category articles are already in the database, if not, then add them.
                            Cursor articleCursor = searchHelper.findByCategory(fragTitle.toLowerCase());
                            if (articleCursor.getCount() == 0) {
                                for (Article article : articles) {
                                    int articleId = Integer.parseInt(article.getArticleId());
                                    String articleTitle = article.getArticleTitle();
                                    String articleCategory = article.getArticleCategory();
                                    String articleTimeStamp = String.valueOf(article.getArticleTimeStamp());
                                    // adds articles to database based on users preference notifications
                                    //searchHelper.insertArticles(articleId, articleTitle, articleCategory, articleTimeStamp);
                                }
                            }
                        }

                        int currentSize = articleAdapter.getItemCount();
                        articleAdapter.notifyItemRangeInserted(currentSize,articlesNew.size());
                        alphaAdapter.notifyItemRangeInserted(currentSize, articlesNew.size());                    }

                    @Override
                    public void onFailure(Call<ArticleArray> call, Throwable t) {
                    }
                });
            }
        }
    }

    @Override
    public void onLastArticleShown(int position) {
        if(fragTitle.equals("Bookmarks"))return;
        displayLatestArticles((position+1)/20);
    }

    private void makeRV (){
        articleAdapter = new ArticleAdapter(articles,this,this);
        alphaAdapter = new AlphaInAnimationAdapter(articleAdapter);
        alphaAdapter.setDuration(3000);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        articleRV.setAdapter(alphaAdapter);
        RV_SpaceDecoration decoration = new RV_SpaceDecoration(15);
        articleRV.addItemDecoration(decoration);
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            gridLayoutManager = new GridLayoutManager(getContext(),1);
        }
        else if (getResources().getConfiguration().orientation == 1)gridLayoutManager = new GridLayoutManager(getContext(), 2);
        else gridLayoutManager = new GridLayoutManager(getContext(), 3);
        articleRV.setLayoutManager(gridLayoutManager);
        articleRV.setHasFixedSize(true);
    }

    @Override
    public void onRVItemClick(Article article) {

        Intent intent = new Intent(getActivity(), ArticleActivity.class);
        intent.putExtra("ID_KEY", article.getArticleId());
        intent.putExtra("TITLE_KEY", article.getArticleTitle());

        startActivity(intent);
    }


    @Override
    public void getResponse(ArrayList<Article> articleArrayList) {
        boolean isEqual = true;
        for (int i = 0; i< articleArrayList.size(); i++) {
           if(!articles.contains(articleArrayList.get(i))) {
               isEqual = false;
           }
        }
        if (articleArrayList.size() != articles.size()) {
            isEqual = false;
        }

        if (isEqual)return;

        articles.clear();
        articles.addAll(articleArrayList);
        articleAdapter.notifyDataSetChanged();
        alphaAdapter.notifyDataSetChanged();
    }


    //setter for notification prefs to pass to fragment to save category articles that users wants notices for
    public void setNotificationString(String notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
}
