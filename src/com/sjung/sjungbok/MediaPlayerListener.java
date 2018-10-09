package com.sjung.sjungbok;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.MenuItem;

/**
 * Created by Fredrik on 2015-03-08.
 */
public class MediaPlayerListener implements MediaPlayer.OnCompletionListener{
    MenuItem musicIcon;
    Context context;
    public MediaPlayerListener(MenuItem musicIcon, Context context){
        this.musicIcon=musicIcon;
        this.context=context;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        System.out.println("låten är klar och jag spelades upp");
        musicIcon.setIcon(context.getResources().getDrawable(R.drawable.greynot));
    }
}
