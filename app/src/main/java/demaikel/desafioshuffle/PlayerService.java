package demaikel.desafioshuffle;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerService extends Service {

    private final IBinder binder = new LocalBinder();
    private List<Song> songList = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int position = 0;


    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    public void setSongs(){
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC;

        Cursor cursor = getContentResolver().query(uri, projection, selection, null, null);
       /*Commented is for teaching*/
       /*Cursor cursor = getContentResolver().query(uri, projection, selection, null, MediaStore.Audio.Media.TITLE + " DESC");*/
        if (cursor != null) {
            while (cursor.moveToNext()) {
                songList.add(new Song(cursor.getString(0), cursor.getString(1) + " - " + cursor.getString(2)));
               /*Log.d("SONG_NAME", cursor.getString(1));*/
            }
            cursor.close();
        }
    }


    private long random() {
        int size = songList.size();
        if (size == 1) {
            this.position = 0;
            return songList.get(0).getId();
        } else {
            int min = 0;
            Random random = new Random();
            int position = random.nextInt(size-min)+min;
            long songId = songList.get(position).getId();
            this.position = position;
            return songId;
        }
    }

    public void playSong() {
        if (songList.size() > 0) {
            Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, random());
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), songUri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        //updateUser();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playSong();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
