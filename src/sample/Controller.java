package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.Model.Album;
import sample.Model.Artist;
import sample.Model.DataSource;
import sample.Model.Song;

public class Controller {

    @FXML
    private TableView artistTable;

    @FXML
    private TableColumn landingPage;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button albumsButton;

    @FXML
    private Button songsButton;

    @FXML
    public void onSelectedRow() {
        if (artistTable.getSelectionModel().getSelectedItem() instanceof Artist)
            albumsButton.setDisable(false);

        if (artistTable.getSelectionModel().getSelectedItem() instanceof Album)
            songsButton.setDisable(false);
    }

    @FXML
    public void listArtist() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);

        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            albumsButton.setDisable(true);
            songsButton.setDisable(true);
        });

        task.setOnFailed(e -> {
            landingPage.setText("Artists");
            progressBar.setVisible(false);
        });

        new Thread(task).start();
    }

    @FXML
    public void listAlbumsForArtist() {
        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if (artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }

        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(
                        DataSource.getInstance().queryAlbumForArtistId(artist.getId()));
            }
        };

        artistTable.itemsProperty().bind(task.valueProperty());
        task.setOnSucceeded(e -> {
            landingPage.setText("Albums By the Artist:  " + artist.getName());
            albumsButton.setDisable(true);
        });

        new Thread(task).start();
    }

    @FXML
    public void listSongsForAlbum() {
        final Album album = (Album) artistTable.getSelectionModel().getSelectedItem();
        if (album == null) {
            System.out.println("NO ALBUM SELECTED");
            return;
        }

        Task<ObservableList<Song>> task = new Task<ObservableList<Song>>() {
            @Override
            protected ObservableList<Song> call() throws Exception {
                return FXCollections.observableArrayList(
                        DataSource.getInstance().querySongsForAlbumId(album.getId()));
            }
        };

        task.setOnSucceeded(event -> {
            landingPage.setText("Songs from the Album:  " + album.getName());
            songsButton.setDisable(true);
        });
        artistTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }
}

class GetAllArtistsTask extends Task {
    @Override
    public ObservableList<Artist> call() throws Exception {
        return FXCollections.observableArrayList
                (DataSource.getInstance().queryArtists(DataSource.ORDER_BY_ASC));
    }
}


