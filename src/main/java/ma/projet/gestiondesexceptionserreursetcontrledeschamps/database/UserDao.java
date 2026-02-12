package ma.projet.gestiondesexceptionserreursetcontrledeschamps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ma.projet.gestiondesexceptionserreursetcontrledeschamps.models.Utilisateur;

public class UserDao {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NOM, DatabaseHelper.COLUMN_AGE, DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_PASSWORD };

    public UserDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Utilisateur createUser(String nom, int age, String email, String password) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOM, nom);
        values.put(DatabaseHelper.COLUMN_AGE, age);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);
        long insertId = db.insert(DatabaseHelper.TABLE_USER, null,
                values);
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Utilisateur newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public Utilisateur getUserByEmailAndPassword(String email, String password) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, allColumns,
                DatabaseHelper.COLUMN_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                new String[]{email, password}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                Utilisateur user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
            cursor.close();
        }

        return null;
    }

    private Utilisateur cursorToUser(Cursor cursor) {
        Utilisateur user = new Utilisateur(cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4));
        user.setId(cursor.getInt(0));
        return user;
    }
}