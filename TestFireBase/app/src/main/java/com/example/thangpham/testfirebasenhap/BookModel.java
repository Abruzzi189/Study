package com.example.thangpham.testfirebasenhap;

/**
 * Created by ThangPham on 12/22/2017.
 */

public class BookModel {
    public String id;
    public String title;
    public String author;


    public BookModel(String id, String title, String author) {

        this.id = id;
        this.title = title;
        this.author = author;
    }

    public BookModel(String title, String author) {
        this.title = title;
        this.author = author;
    }
    public BookModel()
    {

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "BookModel{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
