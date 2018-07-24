package com.example.android.newsreader_android_app_abnd_project;

import android.graphics.Bitmap;

/**
 * Created by Ken Muckey on 7/6/2018.
 */
class Article {

    //initialize variables
    private String webTitle;
    private String articleURL;
    private String publishedDataTime;
    private Bitmap imageUrl; //note:BITMAP converted from String in QueryUtils
    private String authorName;
    private String authorSurname;
    private String articleSection;

    /**
     * Constructs a new {@link Article} object.
     *
     * @param webTitleInput          Title of result
     * @param publishedDataTimeInput Date time of result
     * @param articleURLInput        web link of result
     */
    public Article(String webTitleInput, String publishedDataTimeInput, String articleURLInput,
                   Bitmap imageUrlInput, String authorNameInput, String authorSurnameInput, String articleSectionInput) {
        webTitle = webTitleInput;
        publishedDataTime = publishedDataTimeInput;
        articleURL = articleURLInput;
        imageUrl = imageUrlInput;
        authorName = authorNameInput;
        authorSurname = authorSurnameInput;
        articleSection = articleSectionInput;
    }

    /*
    Set Methods
     */
    public void setMyLocation(String webTitleInput) {
        webTitle = webTitleInput;
    }

    public void setMyTime(String publishedDataTimeInput) {
        publishedDataTime = publishedDataTime;
    }

    public void setMyURL(String articleURLInput) {
        articleURL = articleURLInput;
    }

    public void setimageUrl(Bitmap imageUrlInput) {
        imageUrl = imageUrlInput;
    }

    public void setauthorName(String authorNameInput) {
        authorName = authorNameInput;
    }

    public void setauthorSurname(String authorSurnameInput) {
        authorSurname = authorSurnameInput;
    }

    public void setarticleSection(String articleSectionInput) {
        articleSection = articleSectionInput;
    }

    /*
    GET Methods
     */
    public String getMyWebTitle() {
        return webTitle;
    }

    public String getMyPublishedDataTime() {
        return publishedDataTime;
    }

    public String getMyArticleURL() {
        return articleURL;
    }

    public Bitmap getMyimageUrl() {
        return imageUrl;
    }

    public String getauthorName() {
        return authorName;
    }

    public String getauthorSurname() {
        return authorSurname;
    }

    public String getarticleSection() {
        return articleSection;
    }
}
