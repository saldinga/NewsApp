package com.example.android.newsapp;

public class News {

    private String mSectionName;
    private String mPublicationDate;
    private String mTitle;
    private String mUrl;
    private String mTrailText;
    private String mAuthors;

    public News(String sectionName, String publicationDate, String title, String url,
                String trailText, String authors) {
        mSectionName = sectionName;
        mPublicationDate = publicationDate;
        mTitle = title;
        mUrl = url;
        mTrailText = trailText;
        mAuthors = authors;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getPublicationDate() {
        return mPublicationDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTrailText() {
        return mTrailText;
    }

    public String getAuthors() {
        return mAuthors;
    }
}
