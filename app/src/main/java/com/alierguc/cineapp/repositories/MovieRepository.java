package com.alierguc.cineapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alierguc.cineapp.models.MovieModel;
import com.alierguc.cineapp.network.MovieApiClient;

import java.util.List;

public class MovieRepository {

    private static MovieRepository instance;

    private MovieApiClient movieApiClient;
    public static MovieRepository getInstance(){
        if(instance == null){
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository(){
      movieApiClient = MovieApiClient.getInstance();
    }

    private String movieQuery;
    private int moviePageNumber;
    private String movieLanguage;

    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }

    // Repository içindeki metodu çağır.
    public void searchMovieApi(String query,int pageNumber,String language){
        movieQuery = query;
        moviePageNumber = pageNumber;
        movieLanguage = language;
        movieApiClient.searchMoviesApi(query,pageNumber,language);
    }

    public void searchNextPage(){
        searchMovieApi(movieQuery,moviePageNumber+1,"tr");
    }

}
