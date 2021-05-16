package com.alierguc.cineapp.network;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alierguc.cineapp.AppExecutors;
import com.alierguc.cineapp.models.MovieModel;
import com.alierguc.cineapp.response.MovieSearchResponse;
import com.alierguc.cineapp.utils.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {
    private MutableLiveData<List<MovieModel>> mutableMovies;
    private static MovieApiClient instance;
    // Global RUNNABLE YAP
    private RetrieveMoviesRunnable retrieveMoviesRunnable;


    public static MovieApiClient getInstance(){
        if(instance == null){
            instance = new MovieApiClient();
        }
        return instance;
    }

    private MovieApiClient(){
        mutableMovies = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return mutableMovies;
    }

    public void searchMoviesApi(String query,int pageNumber,String language) {
        if(retrieveMoviesRunnable != null){
            retrieveMoviesRunnable = null;
        }

        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query,pageNumber,language);

        final Future myHandler = AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                myHandler.cancel(true);
                // Çağrı İptal Edildiğinde
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }
        private class RetrieveMoviesRunnable implements Runnable{
            private String query;
            private int pageNumber;
            private String language;
            boolean cancelRequest;

            public RetrieveMoviesRunnable(String query, int pageNumber,String language) {
                this.query = query;
                this.language = language;
                this.pageNumber = pageNumber;
                cancelRequest = false;
            }

            @Override
            public void run() {
                try{
                    Response response = getMovies(query,pageNumber,language).execute();
                    if (cancelRequest) {
                        return;
                    }
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response.body()).getMovies());
                    if(response.code() == 200){

                        if(pageNumber ==1){
                            // Veriyi Live Data'ya Gönder.
                            // PostValue : Background Threading Kullan.
                            // SetValue : Background Threading Kullanma.
                            mutableMovies.postValue(list);

                        }else{
                            List<MovieModel> currentMovies = mutableMovies.getValue();
                            currentMovies.addAll(list);
                            mutableMovies.postValue(currentMovies);
                        }
                    }
                    else{
                        String error = response.errorBody().string();
                        Log.v("Tag","Error"+error);
                        mutableMovies.postValue(null);
                    }
                }
                catch (Exception ex){
                    mutableMovies.postValue(null);
                }
            }
                private Call<MovieSearchResponse> getMovies(String query,int pageNumber,String language){
                    return Service.getMovieApi().searchMovie(
                            Credentials.API_KEY,
                            query,
                            pageNumber,
                            language
                    );

            }
            private void cancelRequest(){
                Log.v("Tag","Arama İptal Edildi");
                cancelRequest = true;
            }
        }

}

