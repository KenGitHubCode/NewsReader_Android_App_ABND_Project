package com.example.android.newsreader_android_app_abnd_project;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Ken Muckey on 7/6/2018.
 */
class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param articles A List of Article objects to display in a list
     */
    public ArticleAdapter(Activity context, ArrayList<Article> articles) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, articles);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //populate View holders /*************************************************************/
        ViewHolder.authorTextView = listItemView.findViewById(R.id.list_item_author);
        // Find the TextView with view ID date
        ViewHolder.dateView = (TextView) listItemView.findViewById(R.id.list_item_date);
        // Get the webTitle of the result and assign to view
        ViewHolder.webtitleTextView = listItemView.findViewById(R.id.list_item_webtitle);
        // Get the THUMBNAIL image of the result and assign to view
        ViewHolder.backgroundImage = listItemView.findViewById(R.id.background_img);
        // Get the section of the result and assign to view
        ViewHolder.sectionTextView = listItemView.findViewById(R.id.list_item_section);

        // Get the {@link Article} object located at this position in the list
        Article currentArticle = getItem(position);

        //AUTHOR NAME INPUT
        //Parse the input into two  variables to display
        String authorName = "";
        if (currentArticle.getauthorSurname() != null && currentArticle.getauthorName() != null) {
            authorName = parseAuthorName(currentArticle.getauthorName(), currentArticle.getauthorSurname());
        } else {
            ViewHolder.authorTextView.setVisibility(View.GONE);
        }
        // Get the version number from the current Article object and set this text on the number TextView
        ViewHolder.authorTextView.setText(authorName);

        // Date and Time in place list item
        String articleDate = "Published: " + currentArticle.getMyPublishedDataTime().substring(0, 10);

        // Display the date of the current article in that TextView
        ViewHolder.dateView.setText(articleDate);

        // Get the webTitle of the result and assign to view
        String webTitle = currentArticle.getMyWebTitle();
        ViewHolder.webtitleTextView.setText(webTitle);

        // Get the THUMBNAIL image of the result and assign to view
        ViewHolder.backgroundImage.setImageBitmap(currentArticle.getMyimageUrl());

        // Get the section of the result and assign to view
        String sectionText = " in: " + currentArticle.getarticleSection();
        ViewHolder. sectionTextView.setText(sectionText);

        // Return the whole list item layout ***********************************/
        return listItemView;
    }

    /**
     * Parshes the text with fill words
     * @param surname
     * @param name
     * @return
     */
    private String parseAuthorName(String surname, String name) {
        String fullName = ("By: " + name + ", " + surname + ". ");
        return fullName;
    }

}

