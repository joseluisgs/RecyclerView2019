package com.example.recyclerview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // Como lo vamos a hacer asincrono debemos sacar las cosas arriba
    private RecyclerView recyclerView;
    private MyListAdapter adapter;

    private ArrayList<MyListData> myListData;

    // Para refrescar
    private SwipeRefreshLayout swipeRefreshLayout;

    // Tarea en segundo plano
    private MiTarea2Plano tarea;

    // Deslizamientos horizontales
    private Paint p = new Paint();

    // Snakcbar
    CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Iniciamos la vista
        iniciarVista();


    }

    private void iniciarVista() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem(new MyListData("Prueba55", android.R.drawable.ic_dialog_map));

            }
        });

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);


        // Cargamos los datos en 2Plano

        // Hay que pasarle el parñametro, puede ser un string si lo definimos asi en la clase
        // Le he pasado 10, para que sea 10 segundos lo que espere
        // Pero podría ser un estring de una dirección de internet,
        tarea = new MiTarea2Plano();
        tarea.execute(2);


        // Un ejemplo del proceso: https://stacktips.com/tutorials/android/android-recyclerview-example
        //myListData = cargarDatos();

        // Creamos el recycler del tipo que queremos
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        // Lo quitamos de aqui y lo llevamos al post ejecute
        //adapter = new MyListAdapter(myListData);
        // Tiene distintos modos de presentación, este es Linear, pero esta el Gri
        //recyclerView.setAdapter(adapter);


        // Iniciamos el Swiper de Recargar
        iniciarSwipeRecargar();

        // Iniciamos el Swipe Horizontal
        iniciarSwipeHorizontal();

    }

    private void iniciarSwipeRecargar() {
        // Para refrescar y volver al cargar
        //https://medium.com/@alvareztech/pull-to-refresh-en-tu-lista-recyclerview-en-android-9f2ce5657b30
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Cambiamos colores si queremos, se ve un poco feo así, pero...
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
                // Volvemos a cargar los datos
                tarea = new MiTarea2Plano();
                tarea.execute(4);

            }
        });
    }

    private void iniciarSwipeHorizontal() {
        //https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            // Evento al mover
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Voy a hacer lo mismo en las dos
                // Si nos movemos a la izquierda
                if (direction == ItemTouchHelper.LEFT) {
                    borrarElemento(position);
                    //final MyListData deletedModel = myListData.get(position);
                    //final int deletedPosition = position;
                    //adapter.removeItem(position);
                    // Mostramos la barra

                    /*
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, " eliminado de la lista!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("DESHACER", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            adapter.restoreItem(deletedModel, deletedPosition);
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();

                    */
                    // Si es a la derecha
                } else {
                    editarElemento(position);

                }
            }

            // Dibujamos los botones y evenetos
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    // Si es dirección a la derecha: izquierda->derecta
                    if (dX > 0) {
                        p.setColor(Color.BLUE);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                        // Caso contrario
                    } else {
                        p.setColor(Color.RED);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void borrarElemento(int position) {
        final MyListData deletedModel = myListData.get(position);
        final int deletedPosition = position;
        adapter.removeItem(position);
        // Mostramos la barra
        Snackbar snackbar = Snackbar.make(coordinatorLayout, " eliminado de la lista!", Snackbar.LENGTH_LONG);
        snackbar.setAction("DESHACER", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                adapter.restoreItem(deletedModel, deletedPosition);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void editarElemento(int position){

        // https://inducesmile.com/android-programming/how-to-add-edittext-in-alert-dialog-programmatically-in-android/

        final MyListData editedModel = myListData.get(position);
        final int editedPosition = position;
        adapter.removeItem(position);

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);

        final TextView texto = (TextView) dialogView.findViewById(R.id.txtNombre);
        final EditText descripcion = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button bAceptar = (Button) dialogView.findViewById(R.id.bAceptar);
        Button bCancelar = (Button) dialogView.findViewById(R.id.bCancelar);

        texto.setText("Nuevo nombre para: "+editedModel.getDescription());

        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                adapter.restoreItem(editedModel, editedPosition);

            }
        });
        bAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editedModel.setDescription(descripcion.getText().toString());
                dialogBuilder.dismiss();
                adapter.restoreItem(editedModel, editedPosition);

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // http://www.sgoliver.net/blog/tareas-en-segundo-plano-en-android-i-thread-y-asynctask/
    //SimpleTask se extiende de AsyncTask que además de ser abstracta es genérica.
    // Las tres variables de entrada que posee se refieren a los Parámetros, Unidades de Progreso y Resultados respectivamente.
    //La clase AsyncTask posee métodos te permitirán coordinar la ejecución de las tareas que
    // deseas ubicar en segundo plano. Estos métodos tienen los siguientes propósitos:

    class MiTarea2Plano extends AsyncTask<Integer, Integer, Integer> {

        private ProgressDialog progreso;
        private ProgressBar progressBar;

        //onPreExecute(): En este método van todas aquellas instrucciones que se ejecutarán antes de iniciar la tarea en segundo plano.
        // Normalmente es la inicialización de variables, objetos y la preparación de componentes de la interfaz.
        @Override
        protected void onPreExecute() {
            // Saco la barra de progreso
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            // O un diálogo
            progreso = new ProgressDialog(MainActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progreso.setMessage("Cargando...");
            progreso.setCancelable(false);
            progreso.setMax(100);
            progreso.setProgress(0);
            progreso.show();

        }

        //doInBackground(Parámetros…): Recibe los parámetros de entrada para ejecutar las instrucciones especificas que irán en segundo plano,
        // luego de que ha terminado onPreExecute(). Dentro de él podemos invocar un método auxiliar llamado publishProgress(),
        // el cual transmitirá unidades de progreso al hilo principal.
        // Estas unidades miden cuanto tiempo falta para terminar la tarea,
        // de acuerdo a la velocidad y prioridad que se está ejecutando.
        @Override
        protected Integer doInBackground(Integer... param) {
            int res = 1;
            //Este es un for para jugar y relentizar
            for (int i = 1; i <= param[0]; i++) {
                res *= i;
                SystemClock.sleep(1000); // Thread.sleep();
                publishProgress(i * 100 / param[0]);

            }

            // Lo cargamos
            myListData = cargarDatos();

            // O uno a uno
            myListData.add(new MyListData("Prueba", android.R.drawable.ic_dialog_map));

            return res;

        }

        //onProgressUpdate(Progreso…): Este método se ejecuta en el hilo de UI luego de que publishProgress()
        // ha sido llamado. Su ejecución se prolongará lo necesario hasta que la tarea en segundo plano
        // haya sido terminada. Recibe las unidades de progreso, así que podemos usar algún View
        // para mostrarlas al usuario para que este sea consciente de la cantidad de tiempo que debe esperar.
        @Override
        protected void onProgressUpdate(Integer... prog) {
            progreso.setProgress(prog[0]);

        }

        //onPostExecute(Resultados…): Aquí puedes publicar todos los resultados retornados por doInBackground()
        // hacia el hilo principal.
        @Override
        protected void onPostExecute(Integer res) {
            progreso.dismiss();
            progressBar.setVisibility(View.GONE);

            // Mostramos el recycler
            //myListData = cargarDatos();
            // Podemos pasarel el arrayList completo
            adapter = new MyListAdapter(myListData);
            recyclerView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
            recyclerView.setHasFixedSize(true);


            //salida.append(res + "\n");
            // Desactivamos
            swipeRefreshLayout.setRefreshing(false);


        }

        // onCancelled(): Ejecuta las instrucciones que desees que se realicen al cancelar la tarea asíncrona.
        @Override
        protected void onCancelled() {

            //salida.append("cancelado\n");

        }

    }

    private ArrayList<MyListData> cargarDatos() {
        ArrayList<MyListData> lista = new ArrayList<MyListData>();

        lista.add(new MyListData("Email", android.R.drawable.ic_dialog_email));
        lista.add(new MyListData("Info", android.R.drawable.ic_dialog_info));
        lista.add(new MyListData("OOOOHHHHH", android.R.drawable.ic_delete));
        lista.add(new MyListData("Dialer", android.R.drawable.ic_dialog_dialer));
        lista.add(new MyListData("Alert", android.R.drawable.ic_dialog_alert));
        lista.add(new MyListData("Map", android.R.drawable.ic_dialog_map));
        lista.add(new MyListData("Email", android.R.drawable.ic_dialog_email));
        lista.add(new MyListData("Info", android.R.drawable.ic_dialog_info));
        lista.add(new MyListData("Delete", android.R.drawable.ic_delete));
        lista.add(new MyListData("Dialer", android.R.drawable.ic_dialog_dialer));
        lista.add(new MyListData("Alert", android.R.drawable.ic_dialog_alert));
        lista.add(new MyListData("Map", android.R.drawable.ic_dialog_map));

        return lista;
    }





}
