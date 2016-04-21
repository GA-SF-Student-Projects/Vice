package martell.com.vice.dbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anthony on 4/20/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArticleNotices";

    Cursor cursor;
    SQLiteDatabase dbWrite = getWritableDatabase();
    SQLiteDatabase dbRead = getReadableDatabase();

    // table and columns
    public static final String ARTICLES_TABLE_NAME = "articles";
    public static final String COL_ID = "_id";
    public static final String COL_ARTICLE_ID = "article_id";
    public static final String COL_ARTICLE_NAME = "name";
    public static final String COL_ARTICLE_CATEGORY = "name";
    public static final String COL_ARTICLE_TIMESTAMP = "timestamp";

    // builds all columns in one array for queries later on
    public static final String[] COLUMNS = {COL_ID, COL_ARTICLE_ID, COL_ARTICLE_NAME,
            COL_ARTICLE_CATEGORY, COL_ARTICLE_TIMESTAMP};

    // the actual sql statement to create the table
    public static final String CREATE_ARTICLES_TABLE = "CREATE TABLE IF NOT EXISTS " + ARTICLES_TABLE_NAME +
            " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COL_ARTICLE_ID + " INT, " +
            COL_ARTICLE_NAME + " TEXT, " +
            COL_ARTICLE_CATEGORY + " TEXT, " +
            COL_ARTICLE_TIMESTAMP + " INT );";

    // makes sure there is only one instance of the database
    // if there isn't one, make it, otherwise return the one instance
    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    // database constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ARTICLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_ARTICLES_TABLE);
        this.onCreate(db);
    }

    public void insertArtcles() {

    }

    public void updateArticles() {

    }

    public void findArticlesl() {

    }

    public void deleteArticles() {

    }
}
