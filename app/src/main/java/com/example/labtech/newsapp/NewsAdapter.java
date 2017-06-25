package com.example.labtech.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each article
 * in the data source (a list of {@link Article} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAdapter extends ArrayAdapter<Article> {
    private final Context context;

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context  of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
        this.context = context;
    }

    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of articles.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        // Find the article at the given position in the list of articles
        Article currentArticle = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        //reuse view if it already exists
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            //inflate new view with custom list_item layout
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.article_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // set this text on the Title TextView
        holder.titleTextView.setText(currentArticle.getTitle());
        // set this text on the section TextView
        holder.sectionTextView.setText(currentArticle.getSection());
        // set this text on the date TextView
        holder.dateTextView.setText(currentArticle.getDatePublished());
        // set this text on the Contributor TextView
        holder.contributorTextView.setText(currentArticle.getContributor());

        // Return the list item view that is now showing the appropriate data
        return convertView;
    }

    /**
     * ViewHolder
     * binds views and fields to a variable
     * no need to use findViewById anymore
     */
    static class ViewHolder {
        // Get the title  from the current article object and
        @BindView(R.id.title_text_view)
        TextView titleTextView;
        // Get the section  from the current article object and
        @BindView(R.id.section_text_view)
        TextView sectionTextView;
        // Get the date  from the current article object and
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        // Get the contributor  from the current article object and
        @BindView(R.id.contributor_text_view)
        TextView contributorTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}