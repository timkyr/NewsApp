package com.example.labtech.newsapp;

/**
 * Created by LABTECH on 24/6/2017.
 */

public class Article {


    /**
     * Title of the news article
     */
    private String title;

    /**
     * contributor of the news article
     */
    private String contributor;

    /**
     * article's date of publication
     */
    private String datePublished;

    /**
     * Author of the article's section
     */
    private String section;

    /**
     * Website URL of the article
     */
    private String url;


    /**
     * Constructs a new {@link Article} object.
     *
     * @param title         is the Title of the book
     * @param contributor   is the Author of the book
     * @param datePublished Thumbnail image of the cover of the book
     * @param section       Thumbnail image of the cover of the book
     * @param url           Website URL of the article
     */
    public Article(String title, String contributor, String datePublished, String section, String url) {
        this.title = title;
        this.contributor = contributor;
        this.datePublished = datePublished;
        this.section = section;
        this.url = url;
    }


    /**
     * Returns the Title of the article
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the Author of the article
     */
    public String getContributor() {
        return contributor;
    }

    /**
     * Returns the date of publication of the article
     */
    public String getDatePublished() {
        return datePublished;
    }

    /**
     * Returns the section of the article
     */
    public String getSection() {
        return section;
    }

    /**
     * Returns theweb url of the article
     */
    public String getUrl() {
        return url;
    }

}
