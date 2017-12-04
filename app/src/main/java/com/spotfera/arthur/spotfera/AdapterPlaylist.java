package com.spotfera.arthur.spotfera;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by arthur on 12/09/17.
 */

public class AdapterPlaylist extends BaseAdapter {

    private Context context;
    private List<String> nomesPlaylist;
    private List<String> urlPlaylist;
    private List<Bitmap> imagensPlayList;

    ImageLoader imageLoader;

    public AdapterPlaylist(Context context)
    {
        super();
        this.context = context;
    }

    private static class ViewHolder {
        int position;
        String imageURL;
        Bitmap bitmap;
        ImageView imageView;
        TextView textView;
    }


    public void setListaNomes(List<String> nomes)
    {
        nomesPlaylist = nomes;
    }

    public void setListaImagens(List<Bitmap> imgs)
    {
        imagensPlayList = imgs;
    }

    public void setListaImagensUrl(List<String> imgsUrls)
    {
        urlPlaylist = imgsUrls;
    }

    @Override
    public int getCount() {
        return nomesPlaylist.size();
    }

    @Override
    public Object getItem(int i) {
        return nomesPlaylist.get(i);
    }

    public Object getImg(int i)
    {
        return imagensPlayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_playlist, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.playlist_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.playlist_text);
            convertView.setTag(viewHolder);

            imageLoader = ImageLoader.getInstance();
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.position = i;
        viewHolder.imageURL = urlPlaylist.get(i);
        viewHolder.textView.setText(nomesPlaylist.get(i));
        new DownloadAsyncTask().execute(viewHolder);

        return convertView;
    }

    private class DownloadAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {

        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {

            ViewHolder viewHolder = params[0];

            if(viewHolder.imageURL == null)
                viewHolder.bitmap = null;
            else
                //viewHolder.bitmap = imageLoader.loadImageSync(viewHolder.imageURL);
                viewHolder.bitmap = Utils.downloadImage(imageLoader, viewHolder.imageURL, null);

            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {
            if (result.bitmap == null) {
                result.imageView.setImageResource(R.drawable.playlist_icon);
            } else {
                result.imageView.setImageBitmap(result.bitmap);
            }
        }
    }

}
