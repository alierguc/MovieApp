package com.alierguc.cineapp.viewmodels;

import android.graphics.Movie;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alierguc.cineapp.models.MovieModel;
import com.alierguc.cineapp.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {

    private MovieRepository movieRepository;

    public MovieListViewModel(){
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieRepository.getMovies();
    }

    // View-model'in metodunu Çağır

    public void searchMovieApi(String query,int pageNumber,String language){
        movieRepository.searchMovieApi(query,pageNumber,language);
    }

    public void searchNextPage(){
        movieRepository.searchNextPage();
    }

}
