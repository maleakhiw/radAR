package Model;

import java.io.Serializable;

/**
 * Created by keyst on 18/09/2017.
 * This represent blueprints for the movie
 */

public class Movie implements Serializable {
    private String poster;
    private String title;
    private String movieType;
    private String year;
    private String imdbID;

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String type) {
        this.movieType = type;
    }

    public Movie() {
    }
}
