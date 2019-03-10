package com.example.jcmilena.jdainventario;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ControllerActivity extends AppCompatActivity implements AddEquipoFragment.OnAddEquipoListener, InventarioFragment.OnInventarioFragmentListener, SearchFragment.OnSearchFragmentListener {

    List<EquipoInformatico> inventario = new ArrayList<>();
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        db = new MiBBDD_Helper(this).getWritableDatabase();

        Fragment fragment = new WelcomeFragment();
        cargar_fragment(fragment);
    }

    private void cargar_fragment(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jdainventario_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.equipo:
                cargar_fragment(new AddEquipoFragment());
                break;
            case R.id.buscar:
                cargar_fragment(new SearchFragment());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void writeSQLite(EquipoInformatico equipo) {
        ContentValues cv = new ContentValues();

        cv.put(MiBBDD_Schema.EntradaBBDD.COLUMNA1, equipo.getFabricante());
        cv.put(MiBBDD_Schema.EntradaBBDD.COLUMNA2, equipo.getModelo());
        cv.put(MiBBDD_Schema.EntradaBBDD.COLUMNA3, equipo.getMAC());
        cv.put(MiBBDD_Schema.EntradaBBDD.COLUMNA4, equipo.getAula());

        db.insert(MiBBDD_Schema.EntradaBBDD.TABLE_NAME, null, cv);

    }

    @Override
    public List<EquipoInformatico> getEquiposList() {
        return inventario;
    }

    @Override
    public void searchSQLite(String columna, String valor) {
        inventario.clear();
        String selection = columna + "=?";
        String[] args = { valor };
        Cursor cursor = db.query(MiBBDD_Schema.EntradaBBDD.TABLE_NAME, null, selection, args, null, null, null );
        while (cursor.moveToNext()) {
            inventario.add(new EquipoInformatico(
                    cursor.getString(cursor.getColumnIndex(MiBBDD_Schema.EntradaBBDD.COLUMNA1)),
                    cursor.getString(cursor.getColumnIndex(MiBBDD_Schema.EntradaBBDD.COLUMNA2)),
                    cursor.getString(cursor.getColumnIndex(MiBBDD_Schema.EntradaBBDD.COLUMNA3)),
                    cursor.getString(cursor.getColumnIndex(MiBBDD_Schema.EntradaBBDD.COLUMNA4))
            ));
        }
        cargar_fragment(new InventarioFragment());
    }
}
