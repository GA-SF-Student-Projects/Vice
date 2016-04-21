package martell.com.vice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import martell.com.vice.ArticleActivity;
import martell.com.vice.ArticleAdapter;
import martell.com.vice.Main2Activity;
import martell.com.vice.MainActivity;
import martell.com.vice.R;
import martell.com.vice.RV_SpaceDecoration;
import martell.com.vice.services.ViceAPIService;
import martell.com.vice.models.Article;
import martell.com.vice.models.ArticleArray;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HEAD;

/**
 * Created by adao1 on 4/19/2016.
 */
public class LatestNewFragment extends Fragment implements ArticleAdapter.OnRVItemClickListener, ArticleAdapter.OnLastArticleShownListener{
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest_news,container,false);
        articleRV = (RecyclerView)view.findViewById(R.id.articleRV);
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

    private void displayLatestArticles(int numPages){
        Call<ArticleArray> call =null;
        fragTitle = getArguments().getString(MainActivity.KEY_FRAGMENT_TITLE);
        Log.d("Frag", "title: " + fragTitle);
        Log.d(TAG, "THIS IS THE FRAGMENT TITLE " + fragTitle);
        if (fragTitle.equals("Home")) {
            call = viceService.latestArticles(numPages);

        } else if(fragTitle.equals("Bookmarks")) {
            Log.d(TAG, "BOOKSMARKS HAS BEEN SELECTED IN DISPLAYLATEST ARTICLES");

        } else {
            Log.d(TAG, "ELSE IS CALLED IN DISPLAYLATEST ARTICLES + CURTITLE " + fragTitle);
            call = viceService.getArticlesByCategory(fragTitle,numPages);

        }

        if (call != null) {
            call.enqueue(new Callback<ArticleArray>() {
                @Override
                public void onResponse(Call<ArticleArray> call, Response<ArticleArray> response) {
                    Article[] articleArray = response.body().getData().getItems();
                    ArrayList<Article> articlesNew = new ArrayList<>(Arrays.asList(articleArray));
                    articles.addAll(articlesNew);

                    int currentSize = articleAdapter.getItemCount();
                    articleAdapter.notifyItemRangeInserted(currentSize,articlesNew.size());
                    alphaAdapter.notifyItemRangeInserted(currentSize,articlesNew.size());
                }

                @Override
                public void onFailure(Call<ArticleArray> call, Throwable t) {
                }
            });
        }

    }

    @Override
    public void onLastArticleShown(int position) {
        displayLatestArticles((position+1)/20);
    }

    private void makeRV (){
        articleAdapter = new ArticleAdapter(articles,this,this);
        alphaAdapter = new AlphaInAnimationAdapter(articleAdapter);
        alphaAdapter.setDuration(3000);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(articleAdapter);
        scaleAdapter.setDuration(1000);
        //scaleAdapter.setInterpolator(new OvershootInterpolator(1f));
        articleRV.setAdapter(alphaAdapter);
        RV_SpaceDecoration decoration = new RV_SpaceDecoration(15);
        articleRV.addItemDecoration(decoration);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        articleRV.setLayoutManager(gridLayoutManager);
        articleRV.setHasFixedSize(true);
//        articleRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                //super.onScrolled(recyclerView, dx, dy);
//                if(dy > 0){
//                    visibleItemCount = gridLayoutManager.getChildCount();
//                    totalItemCount = gridLayoutManager.getItemCount();
//                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
//                    if(loading){
//                        if ((visibleItemCount + pastVisiblesItems)>=totalItemCount){
//                            loading = false;
//                            displayLatestArticles(totalItemCount/20);
//                        }
//                    }
//                }
//            }
//        });

    }

    @Override
    public void onRVItemClick(Article article) {

        Intent intent = new Intent(getActivity(), ArticleActivity.class);
        intent.putExtra("KEY",article.getArticleId());

        startActivity(intent);
    }
}
