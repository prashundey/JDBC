package sample.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataSource {

    /* **************************  DATABASE REFERENCES  **************************** */

    public static final String DB_NAME = "music.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:"
            + System.getProperty("user.dir") + "/" + DB_NAME;

    /* TABLES:
        artists : ( _id, name )
        albums  : ( _id, name, artist[_id] )
        songs   : ( _id, track, title,  album[_id] )
        Link: songs[album] -> albums[_id] -> albums[artist] -> artist[id]
     */

    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;


    /* **************************  QUERY COMMAND CONSTANTS **************************** */

    public static final String QUERY_ALBUMS_BY_ARTIST_START =
            "SELECT " + TABLE_ALBUMS + '.' + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS +
                    " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST +
                    " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
                    " WHERE " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + " = \"";

    public static final String QUERY_ALBUMS_FOR_ARTIST_SORT =
            " ORDER BY " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";


    public static final String QUERY_ARTIST_SORT =
            " ORDER BY " + COLUMN_ARTIST_NAME + " COLLATE NOCASE ";


    public static final String QUERY_ARTIST_FOR_SONG_START =
            "SELECT " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " +
                    TABLE_SONGS + "." + COLUMN_SONG_TRACK + " FROM " + TABLE_SONGS +
                    " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS + "." + COLUMN_SONG_ALBUM + " = " +
                        TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
                    " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "."+ COLUMN_ALBUM_ARTIST + " = " +
                        TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
                    " WHERE " + TABLE_SONGS + "." + COLUMN_SONG_TITLE + " = \"";

    public static final String QUERY_ARTIST_FOR_SONG_SORT =
            " ORDER BY " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME +
                    " COLLATE NOCASE ";

    // View Query
    public static final String TABLE_ARTIST_SONG_VIEW = "artist_list";
    public static final String CREATE_ARTIST_FOR_SONG_VIEW =
             "CREATE VIEW IF NOT EXISTS " +
                     TABLE_ARTIST_SONG_VIEW + " AS SELECT " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
                     TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " AS " + COLUMN_SONG_ALBUM + ", " +
                     TABLE_SONGS + "." + COLUMN_SONG_TRACK + ", " + TABLE_SONGS + "." + COLUMN_SONG_TITLE +
                     " FROM " + TABLE_SONGS +
                     " INNER JOIN " + TABLE_ALBUMS + " ON " + TABLE_SONGS +
                     "." + COLUMN_SONG_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID +
                     " INNER JOIN " + TABLE_ARTISTS + " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST +
                     " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID +
                     " ORDER BY " +
                     TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + ", " +
                     TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + ", " +
                     TABLE_SONGS + "." + COLUMN_SONG_TRACK;

    public static final String QUERY_VIEW_SONG_INFO =  "SELECT " + COLUMN_ARTIST_NAME + ", " +
            COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW +
            " WHERE " + COLUMN_SONG_TITLE + " = \"";

    public static final String QUERY_VIEW_SONG_INFO_PREP = "SELECT " + COLUMN_ARTIST_NAME + ", " +
            COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_TRACK + " FROM " + TABLE_ARTIST_SONG_VIEW +
            " WHERE " + COLUMN_SONG_TITLE + " = ?";


    /* **************************  DATABASE PREPARED STATEMENT CONSTANTS **************************** */

    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTISTS +
            '(' + COLUMN_ARTIST_NAME + ") VALUES(?)";

    public static final String INSERT_ALBUM = "INSERT INTO " + TABLE_ALBUMS +
            '(' + COLUMN_ALBUM_NAME + ", " + COLUMN_ALBUM_ARTIST + ") VALUES(?, ?)";

    public static final String INSERT_SONG = "INSERT INTO " + TABLE_SONGS +
            '(' + COLUMN_SONG_TRACK + ", " + COLUMN_SONG_TITLE + ", " + COLUMN_SONG_ALBUM + ") VALUES(?, ?, ?)";

    public static final String QUERY_ARTIST = "SELECT " + COLUMN_ARTIST_ID + " FROM " +
            TABLE_ARTISTS + " WHERE " + COLUMN_ARTIST_NAME + " = ?";

    public static final String QUERY_ALBUM = "SELECT " + COLUMN_ALBUM_ID + " FROM " +
            TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_NAME + " = ?";

    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM " + TABLE_ALBUMS +
            " WHERE " + COLUMN_ALBUM_ARTIST + " = ? ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";

    public static final String QUERY_SONGS_BY_ALBUM_ID = "SELECT * FROM " + TABLE_SONGS +
            " WHERE " + COLUMN_SONG_ALBUM + " = ? ORDER BY " + COLUMN_SONG_TITLE + " COLLATE NOCASE";

    public static final String UPDATE_ARTIST_NAME = "UPDATE " + TABLE_ARTISTS + " SET " +
            COLUMN_ARTIST_NAME + " = ? WHERE " + COLUMN_ARTIST_ID + " = ?";

    /* CONNECTION */
    private Connection conn;

    /* PREPARED STATEMENTS: Preventing SQL Injection Attacks */
    private PreparedStatement querySongInfoView;
    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;
    private PreparedStatement insertIntoSongs;
    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;
    private PreparedStatement queryAlbumsByArtistId;
    private PreparedStatement querySongsByAlbumId;

    /* **************************  SINGLETON  **************************** */

    private static DataSource instance = new DataSource();

    private DataSource() {}

    public static DataSource getInstance() {
        return instance; // DataSource.getInstance().Method...
    }

    /* **************************  HANDLING CONNECTION **************************** */

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            querySongInfoView = conn.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);
            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS);
            insertIntoAlbums = conn.prepareStatement(INSERT_ALBUM, Statement.RETURN_GENERATED_KEYS);
            insertIntoSongs = conn.prepareStatement(INSERT_SONG);
            queryArtist = conn.prepareStatement(QUERY_ARTIST);
            queryAlbum = conn.prepareStatement(QUERY_ALBUM);
            queryAlbumsByArtistId = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            querySongsByAlbumId = conn.prepareStatement(QUERY_SONGS_BY_ALBUM_ID);
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (querySongInfoView != null) querySongInfoView.close();
            if (insertIntoArtists != null) insertIntoArtists.close();
            if (insertIntoAlbums != null) insertIntoAlbums.close();
            if (insertIntoSongs != null) insertIntoSongs.close();
            if (queryArtist != null) queryArtist.close();
            if (queryAlbum != null) queryAlbum.close();
            if (queryAlbumsByArtistId != null) queryAlbumsByArtistId.close();
            if (querySongsByAlbumId != null) querySongsByAlbumId.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Could not close connection " + e.getMessage());
        }
    }


    /* **************************  QUERY METHODS **************************** */

    public List<Artist> queryArtists(int sortOrder) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ");
        sb.append(TABLE_ARTISTS);
        sb.append(sortOrderSQL(sortOrder, QUERY_ARTIST_SORT));

        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery(sb.toString());) {

            List<Artist> artists = new ArrayList<>();
            while (results.next()) {
                try {
                    Thread.sleep(10); // Sleeping 15ms for Progress Bar UI
                } catch (InterruptedException e) {
                    System.out.println("Interrupted: " + e.getMessage());
                }

                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }

            return artists;
        } catch (SQLException e) {
            System.out.println("Query Failed or Resources Failed: " + e.getMessage());
            return null;
        }
    }

    public List<Album> queryAlbumForArtistId(int id) {
        try {
            queryAlbumsByArtistId.setInt(1, id);
            ResultSet resultSet = queryAlbumsByArtistId.executeQuery();

            List<Album> albums = new ArrayList<>();

            while(resultSet.next()) {
                Album album = new Album();
                album.setId(resultSet.getInt(1));
                album.setName(resultSet.getString(2));
                album.setArtistId(id);
                albums.add(album);
            }
            return albums;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Song> querySongsForAlbumId(int id) {
        try {
            querySongsByAlbumId.setInt(1, id);
            ResultSet resultSet = querySongsByAlbumId.executeQuery();

            List<Song> songs = new ArrayList<>();

            while(resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt(1));
                song.setTrack(resultSet.getInt((2)));
                song.setName(resultSet.getString(3));
                song.setAlbumId(id);
                songs.add(song);

            }
            return songs;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<String> queryAlbumsForArtist(String artistName, int sortOrder) {
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTIST_START);
        sb.append(artistName);
        sb.append("\"");
        sb.append(sortOrderSQL(sortOrder, QUERY_ALBUMS_FOR_ARTIST_SORT));

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            List<String> albums = new ArrayList<>();
            while (resultSet.next()) {
                albums.add(resultSet.getString(1));
            }

            return albums;
        } catch (SQLException e) {
            System.out.println("Query Failed " + e.getMessage());
            return null;
        }
    }

    public void querySongsMetadata() {
        String sql = "SELECT * FROM " + TABLE_SONGS;

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData meta = resultSet.getMetaData();
            int numColumns = meta.getColumnCount();
            for (int i = 1; i < numColumns; i++) {
                System.out.format("COLUMN %d in the SONGS table is named %s\n", i, meta.getColumnName(i));
            }
        } catch (SQLException e) {
            System.out.println("Query failed");
        }
    }


    public boolean createViewForSongArtists() {
        try(Statement statement = conn.createStatement()) {

            statement.execute(CREATE_ARTIST_FOR_SONG_VIEW);
            return true;

        } catch(SQLException e) {
            System.out.println("Create View failed: " + e.getMessage());
            return false;
        }
    }

    private String sortOrderSQL(int sortOrder, String sortOrderQUERY) {
        StringBuilder sort = new StringBuilder();
        if (sortOrder != ORDER_BY_NONE) {
            sort.append(sortOrderQUERY);
            if (sortOrder == ORDER_BY_DESC) {
                sort.append("DESC");
            } else {
                sort.append("ASC");
            }
        }
        return sort.toString();
    }

    /* **************************  COMMIT METHODS **************************** */

    private int insertArtist(String name) throws SQLException {
        queryArtist.setString(1, name);
        ResultSet resultSet = queryArtist.executeQuery();
        if (resultSet.next()) {
            // Check if Artist already exists & return Artist ID
            return resultSet.getInt(1);
        } else {
            // Insert new artist
            insertIntoArtists.setString(1, name);
            int affectedRows = insertIntoArtists.executeUpdate();

            if (affectedRows != 1) // Only 1 row should be affected via creation of new row
                throw new SQLException("Couldn't insert artist");
        }

        ResultSet generatedKeys = insertIntoArtists.getGeneratedKeys();
        if (generatedKeys.next())
            return generatedKeys.getInt(1); // Return artist ID of new entry
        else
            throw new SQLException("Couldn't get _id for artist");
    }

    private int insertAlbum(String name, int artistID) throws SQLException {
        queryAlbum.setString(1, name);
        ResultSet resultSet = queryAlbum.executeQuery();
        if (resultSet.next()) {
            // Check if album already exists & return Artist ID
            return resultSet.getInt(1);
        } else {
            // Insert new album
            insertIntoAlbums.setString(1, name);
            insertIntoAlbums.setInt(2, artistID);
            int affectedRows = insertIntoAlbums.executeUpdate();

            if (affectedRows != 1) // Only 1 row should be affected via creation of new row
                throw new SQLException("Couldn't insert album");
        }

        ResultSet generatedKeys = insertIntoAlbums.getGeneratedKeys();
        if (generatedKeys.next())
            return generatedKeys.getInt(1);
        else
            throw new SQLException("Couldn't get _id for album");
    }

    // Transaction Handles 3 Insertions
    public void insertSong(String title, String artist, String album, int track) {
        try {
            conn.setAutoCommit(false); // Turing off default behavior
            // Fetching Data from artist & album insert methods
            int artistID = insertArtist(artist);
            int albumID = insertAlbum(album, artistID);
            insertIntoSongs.setInt(1, track);
            insertIntoSongs.setString(2, title);
            insertIntoSongs.setInt(3, albumID);

            int affectedRows = insertIntoSongs.executeUpdate();

            if (affectedRows == 1)
                conn.commit();
            else
                throw new SQLException("The song insert failed");

        } catch (Exception e) {
            System.out.println("Insert song exception: " + e.getMessage());
            try {
                System.out.println("Performing ROLLBACK");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Multiple Exceptions committing");
            }

        } finally {
            try {
                System.out.println("Resetting Default Commit Behavior");
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit");
            }
        }
    }

}
