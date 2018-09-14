package android.gpuimage.com.notification;

/**
 * Created by ThoNh on 5/9/2018.
 */

public class MusicModel {

    //region variable
    private String songName;
    private int musicID;
    private String urlImage;
    //endregion


    //region contructor
    public MusicModel(String songName, int musicID, String urlImage) {
        this.songName = songName;
        this.musicID = musicID;
        this.urlImage = urlImage;
    }
    //endregion

    //region function
    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getMusicID() {
        return musicID;
    }

    public void setMusicID(int musicID) {
        this.musicID = musicID;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
    //endregion
}
