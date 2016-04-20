package martell.com.vice.fragment;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import martell.com.vice.ArticleAdapter;
import martell.com.vice.Main2Activity;
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

/**
 * Created by adao1 on 4/19/2016.
 */
public class LatestNewFragment extends Fragment implements ArticleAdapter.OnRVItemClickListener {
    private String TAG = "Latest News Fragment";
    private ArrayList<Article> articles;
    private RecyclerView articleRV;
    public ViceAPIService viceService;
    private ArticleAdapter articleAdapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private Retrofit retrofit;
    private String category;
    private int countViews;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest_news,container,false);
        articleRV = (RecyclerView)view.findViewById(R.id.articleRV);
        countViews = 0;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articles = new ArrayList<>();
        retrofit = new Retrofit.Builder().baseUrl("http://www.vice.com/en_us/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        viceService = retrofit.create(ViceAPIService.class);
        for (int i = 0; i < 5; i++) {
            displayLatestArticles(i);
        }
    }

    private void displayLatestArticles(int numPages){
        countViews +=1;
        Log.d(TAG, "displayLatestArticles: This is the category " + countViews);
        if (countViews == 1) {
            Call<ArticleArray> call = viceService.latestArticles(numPages);
            call.enqueue(new Callback<ArticleArray>() {
                @Override
                public void onResponse(Call<ArticleArray> call, Response<ArticleArray> response) {
                    Article[] articleArray = response.body().getData().getItems();
                    ArrayList<Article> articlesNew = new ArrayList<>(Arrays.asList(articleArray));
                    articles.addAll(articlesNew);
                    makeRV();
                }

                @Override
                public void onFailure(Call<ArticleArray> call, Throwable t) {
                }
            });
        } else if (countViews == 2) {
            Log.d(TAG, "displayLatestArticles: bookmarks");


        } else {
            Log.d(TAG, "displayLatestArticles: all other categories");

        }


    }

    private void makeRV (){
        articleAdapter = new ArticleAdapter(articles,this);
        alphaAdapter = new AlphaInAnimationAdapter(articleAdapter);
        alphaAdapter.setDuration(8000);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(articleAdapter);
        scaleAdapter.setDuration(8000);
        scaleAdapter.setInterpolator(new OvershootInterpolator());
        articleRV.setAdapter(scaleAdapter);
        RV_SpaceDecoration decoration = new RV_SpaceDecoration(10);
        articleRV.addItemDecoration(decoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        articleRV.setLayoutManager(gridLayoutManager);
        articleRV.setHasFixedSize(true);
    }

    @Override
    public void onRVItemClick(Article article) {
        Intent intent = new Intent(getActivity(), Main2Activity.class);
        intent.putExtra("KEY", article.getArticleId());
        startActivity(intent);
    }
}
