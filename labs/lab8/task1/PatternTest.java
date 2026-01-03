package labs.lab8.task1;

import java.util.ArrayList;
import java.util.List;

class Song {
    String title;
    String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title=" + title +
                ", artist=" + artist +
                '}';
    }
}

interface IMP3PlayerState {
    void pressPlay(MP3Player player);
    void pressStop(MP3Player player);
    void pressFWD(MP3Player player);
    void pressREW(MP3Player player);
}

class MP3Player {
    List<Song> songs;
    public int currentSong;
    IMP3PlayerState state;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        this.state = new StopState(); // Start in stopped state
    }

    public void pressPlay() {
        state.pressPlay(this);
    }

    public void pressStop() {
        state.pressStop(this);
    }

    public void pressFWD() {
        state.pressFWD(this);
    }

    public void pressREW() {
        state.pressREW(this);
    }

    public void printCurrentSong() {
        System.out.println(songs.get(currentSong));
    }

    public void setState(IMP3PlayerState state) {
        this.state = state;
    }

    public void nextSong() {
        currentSong = (currentSong + 1) % songs.size();
    }

    public void previousSong() {
        currentSong = (currentSong - 1 + songs.size()) % songs.size();
    }

    @Override
    public String toString() {
        return "MP3Player{" +
                "currentSong = " + currentSong +
                ", songList = " + songs +
                '}';
    }
}

class StopState implements IMP3PlayerState {

    private boolean isFirstStop = true;

    @Override
    public void pressPlay(MP3Player player) {
        System.out.printf("Song %d is playing%n", player.currentSong);
        player.setState(new PlayState());
    }

    @Override
    public void pressStop(MP3Player player) {
        if (isFirstStop) {
            System.out.println("Songs are stopped");
            player.currentSong = 0;
            isFirstStop = false;
        } else {
            System.out.println("Songs are already stopped");
        }
    }

    @Override
    public void pressFWD(MP3Player player) {
        player.nextSong();
        System.out.println("Forward...");
        isFirstStop = true;
    }

    @Override
    public void pressREW(MP3Player player) {
        player.previousSong();
        System.out.println("Reward...");
        isFirstStop = true;
    }
}

class PlayState implements IMP3PlayerState {

    @Override
    public void pressPlay(MP3Player player) {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressStop(MP3Player player) {
        System.out.printf("Song %d is paused%n", player.currentSong);
        player.setState(new StopState());
    }

    @Override
    public void pressFWD(MP3Player player) {
        player.nextSong();
        player.setState(new StopState());
        System.out.println("Forward...");
    }

    @Override
    public void pressREW(MP3Player player) {
        player.previousSong();
        player.setState(new StopState());
        System.out.println("Reward...");
    }
}


public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}

//Vasiot kod ovde
