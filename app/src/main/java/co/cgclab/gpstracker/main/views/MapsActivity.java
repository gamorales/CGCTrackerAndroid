
package co.cgclab.gpstracker.main.views;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.carros.controllers.CarrosController;
import co.cgclab.gpstracker.carros.controllers.ICarrosController;
import co.cgclab.gpstracker.carros.controllers.IVehiculos;
import co.cgclab.gpstracker.carros.models.CarrosModel;
import co.cgclab.gpstracker.main.controllers.ClusterRenderer;
import co.cgclab.gpstracker.main.controllers.GoogleMaps;
import co.cgclab.gpstracker.main.controllers.IGoogleMaps;
import co.cgclab.gpstracker.main.controllers.Markers;
import co.cgclab.gpstracker.main.models.CoordenadasModel;
import co.cgclab.gpstracker.main.models.RutaModel;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IVehiculos {

    private Spinner spVehiculosMap;
    private Button btnMapsCargar;

    private List<String> lVehiculos;

    private GoogleMap mMap;
    private ClusterManager<Markers> markersClusterManager;

    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String userID, imei;
    Map<String, LatLng> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
         * Cuando no esté conectado a Internet, guardará un caché y al detectar la conexión,
         * enviará los datos a la consola de firebase
         */
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // Se obtienen los datos del usuario logueado
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        spVehiculosMap = findViewById(R.id.spVehiculosMap);
        btnMapsCargar = findViewById(R.id.btnMapsCargar);

        btnMapsCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarVehiculoMapa();
            }
        });

        cargarSpinner();
    }

    private void cargarVehiculoMapa() {
        String[] placa = spVehiculosMap.getSelectedItem().toString().split(" - ");

        // Sólo si se escogió un vehículo se hará la consulta
        if (placa.length==2) {
            cars = new HashMap<String, LatLng>();
            markersClusterManager = new ClusterManager<>(this, mMap);
            ClusterRenderer clusterRenderer = new ClusterRenderer(
                    getBaseContext(),
                    mMap,
                    markersClusterManager
            );
            mMap.setOnCameraIdleListener(markersClusterManager);
            mMap.setOnMarkerClickListener(markersClusterManager);
            mMap.setOnInfoWindowClickListener(markersClusterManager);

            final PolylineOptions polylineOptions = new PolylineOptions();

            final DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos/"+placa[0]);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        CarrosModel vehiculo = dataSnapshot.getValue(CarrosModel.class);
                        imei = vehiculo.getImei();

                        // Se consultan los markers del día
                        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
                        Date date = new Date();
                        Query query = firebaseDatabase.getReference(
                            "Coordenadas/"+userID+"/"+imei+"/"+dateFormat.format(date)
                        );
//                        .orderByChild("fecha");
//                        .startAt(dateFormat.format(date));
//                        .endAt(dateFormat.format(date)+"235959");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                if (dataSnapshot2.exists()) {
                                    DecimalFormat dFormatLat  = new DecimalFormat("##.0000");
                                    DecimalFormat dFormatLng  = new DecimalFormat("##.000000");
                                    String latitud="", longitud="";
                                    HashMap<String, LatLng> markersCar = new HashMap<String, LatLng>();
                                    for (DataSnapshot snap: dataSnapshot2.getChildren()) {
                                        CoordenadasModel coordenadasModel = snap.getValue(CoordenadasModel.class);

                                        /*
                                         * Nos saltaremos los registros con las mismas coordenadas
                                         * o cercanas a 3 decimales
                                         */
                                        if (!latitud.equals(dFormatLat.format(coordenadasModel.getLatitud())) &&
                                            !longitud.equals(dFormatLng.format(coordenadasModel.getLongitud()))) {
                                            markersCar.put(
                                                coordenadasModel.getFecha(),
                                                new LatLng(
                                                    Double.parseDouble(dFormatLat.format(coordenadasModel.getLatitud())),
                                                    Double.parseDouble(dFormatLng.format(coordenadasModel.getLongitud()))
                                                )
                                            );
                                        }
                                        Log.i("MapsActivity",
                                                coordenadasModel.getFecha() + ":  " +
                                                        dFormatLat.format(coordenadasModel.getLatitud()) + ", " +
                                                        dFormatLng.format(coordenadasModel.getLongitud())
                                        );
                                        latitud = dFormatLat.format(coordenadasModel.getLatitud());
                                        longitud = dFormatLng.format(coordenadasModel.getLongitud());

                                    }

                                    // Se organiza el HashMap por orden de la fecha
                                    Map<String,LatLng> treeMarkersCar = new TreeMap<String, LatLng>(markersCar);

                                    Log.i("MapsActivity", "CAMBIO");
                                    LatLng carCamera = null;
                                    int contador = 0;
                                    for(HashMap.Entry<String, LatLng> carEntry: treeMarkersCar.entrySet()) {
                                        contador++;

                                        Log.i("MapsActivity",
                                            carEntry.getKey()+" --> "+carEntry.getValue()
                                        );
                                        // Si es el primer o el último registro, el marker será un carro
                                        if (contador==1 || treeMarkersCar.size()==contador) {
                                            markersClusterManager.addItem(
                                                new Markers(
                                                    new MarkerOptions()
                                                        .position(carEntry.getValue())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
                                                )
                                            );
                                            carCamera = carEntry.getValue();
                                        } else {
                                            markersClusterManager.addItem(
                                                new Markers(
                                                    new MarkerOptions().position(carEntry.getValue())
                                                )
                                            );
                                        } /* else if (contador%10==0) {
                                            markersClusterManager.addItem(
                                                    new Markers(
                                                            new MarkerOptions().position(carEntry.getValue())
                                                    )
                                            );
                                        }*/
                                    }

                                    markersClusterManager.cluster();

                                    if (carCamera!=null) {
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(carCamera));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo( 12.0f ));

                                        // Se posiciona la cámara del mapa sobre un marker específico
                                        CameraPosition cameraPosition = CameraPosition
                                                .builder()
                                                .target(carCamera)
                                                .zoom(17)
                                                //.bearing(90) // Rotar de forma circular 90 grados
                                                .build();

                                        mMap.animateCamera(
                                                CameraUpdateFactory.newCameraPosition(cameraPosition),
                                                5000,
                                                null
                                        );

                                        // Set a listener for marker click.
                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {


                                                /*Intent intent = new Intent(
                                                        MapsActivity.this,
                                                        StreetViewActivity.class
                                                );
                                                intent.putExtra("latitud", marker.getPosition().latitude+"");
                                                intent.putExtra("longitud",marker.getPosition().longitude+"");
                                                startActivity(intent);*/

                                                return false;
                                            }
                                        });
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        /*DatabaseReference databaseReference2 = firebaseDatabase.getReference(
                                "Coordenadas/"+userID+"/"+imei
                        );
                        databaseReference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                if (dataSnapshot2.exists()) {
                                    LatLng carCameraOld = null;
                                    HashMap<Double, LatLng> markersCar = new HashMap<Double, LatLng>();
                                    for (DataSnapshot snap: dataSnapshot2.getChildren()) {
                                        CoordenadasModel coordenadasModel = snap.getValue(CoordenadasModel.class);
                                        markersCar.put(
                                            Double.parseDouble(coordenadasModel.getFecha()),
                                            new LatLng(
                                                coordenadasModel.getLatitud(),
                                                coordenadasModel.getLongitud()
                                            )
                                        );
                                    }

                                    LatLng carCamera = null;
                                    int contador = 0;
                                    for(HashMap.Entry<Double, LatLng> carEntry: markersCar.entrySet()) {
                                        contador++;

                                        // Si es el primer o el último registro, el marker será un carro
                                        if (contador==1 || markersCar.size()==contador) {
                                            markersClusterManager.addItem(
                                                new Markers(
                                                    new MarkerOptions()
                                                        .position(carEntry.getValue())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
                                                )
                                            );
                                            mostrarMensaje(
                                                "Cnt: " + String.valueOf(contador)+" , "+
                                                "hora: " + String.valueOf(carEntry.getKey()),
                                                1
                                            );

                                            carCamera = carEntry.getValue();
                                        } else if (contador%10==0) {
                                            markersClusterManager.addItem(
                                                new Markers(
                                                    new MarkerOptions()
                                                        .position(carEntry.getValue())
                                                )
                                            );
                                        }
                                    }

                                    markersClusterManager.cluster();

                                    if (carCamera!=null) {
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(carCamera));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo( 12.0f ));

                                        // Se posiciona la cámara del mapa sobre un marker específico
                                        CameraPosition cameraPosition = CameraPosition
                                                .builder()
                                                .target(carCamera)
                                                .zoom(13)
                                                //.bearing(90) // Rotar de forma circular 90 grados
                                                .build();

                                        mMap.animateCamera(
                                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                                            5000,
                                            null
                                        );

                                        // Set a listener for marker click.
                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker) {
                                                Intent intent = new Intent(
                                                    MapsActivity.this,
                                                    StreetViewActivity.class
                                                );
                                                intent.putExtra("latitud", marker.getPosition().latitude+"");
                                                intent.putExtra("longitud",marker.getPosition().longitude+"");
                                                startActivity(intent);

                                                return false;
                                            }
                                        });
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mostrarMensaje(
                getResources().getString(R.string.select_car),
                1
            );
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Se posiciona la cámara del mapa sobre un marker específico
        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(3.3947997771, -76.539024176)
        ));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 12.0f ));

//        markersClusterManager = new ClusterManager<>(this, mMap);
//        ClusterRenderer clusterRenderer = new ClusterRenderer(
//                getBaseContext(),
//                mMap,
//                markersClusterManager
//        );
//        mMap.setOnCameraIdleListener(markersClusterManager);
//        mMap.setOnMarkerClickListener(markersClusterManager);
//        mMap.setOnInfoWindowClickListener(markersClusterManager);
//

//        // Add a marker in Sydney and move the camera
//        Map<String, LatLng> cars = new HashMap<String, LatLng>();
//        cars.put("Mazda", new LatLng(3.3947997771, -76.539024176));
//        cars.put("Toyota", new LatLng(3.48449473401, -76.508716889));
//        cars.put("Fiat", new LatLng(3.43667949273, -76.546283307));
//        cars.put("Chevrolet", new LatLng(3.40365230191, -76.524707217));
//        cars.put("Ford", new LatLng(3.42577154332, -76.517187875));
//        cars.put("Hyundai", new LatLng(3.48907264454, -76.499586273));
//        cars.put("Renault", new LatLng(3.42907264454, -76.509586273));
//        cars.put("Honda", new LatLng(3.44507264454, -76.5586273));
//        cars.put("Mitzubishi", new LatLng(3.38907264454, -76.521586273));
//        cars.put("De Lorean", new LatLng(3.23907264454, -76.528586273));
//        cars.put("Rolls Royce", new LatLng(3.41907264454, -76.571586273));
//        cars.put("Daewoo", new LatLng(3.407264454, -76.559586273));
//
//        LatLng carCamera = null;
//        for (Map.Entry<String, LatLng> car: cars.entrySet()) {
//            /*mMap.addMarker(
//                    new MarkerOptions()
//                            .title(car.getKey())
//                            .position(car.getValue())
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
//            ).setTag(0);*/
//
//            carCamera = car.getValue();
//            markersClusterManager.addItem(
//                    new Markers(
//                        new MarkerOptions()
//                            .position(new LatLng(car.getValue().latitude, car.getValue().longitude))
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
//                            /*car.getValue().latitude,
//                            car.getValue().longitude,
//                            car.getKey(),
//                            "something",
//                            BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small)*/
//                    )
//            );
//        }
//        markersClusterManager.cluster();
//
//        if (carCamera!=null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(carCamera));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo( 12.0f ));
//
//            // Se posiciona la cámara del mapa sobre un marker específico
//            CameraPosition cameraPosition = CameraPosition
//                    .builder()
//                    .target(carCamera)
//                    .zoom(13)
//                    .bearing(90) // Rotar de forma circular 90 grados
//                    .build();
//
//            // Set a listener for marker click.
//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    Integer clickCount = (Integer) marker.getTag();
//
//                    // Check if a click count was set, then display the click count.
//                    if (clickCount != null) {
//                        clickCount = clickCount + 1;
//                        marker.setTag(clickCount);
//                        mostrarMensaje(
//                                marker.getTitle() +
//                                        " has been clicked " + clickCount + " times.",
//                                1);
//                    }
//
//                    Intent intent = new Intent(
//                            MapsActivity.this,
//                            StreetViewActivity.class
//                    );
//                    intent.putExtra("latitud", marker.getPosition().latitude+"");
//                    intent.putExtra("longitud",marker.getPosition().longitude+"");
//                    startActivity(intent);
//
//                    return false;
//                }
//            });
//        }


        /*LatLng[] cars = {
                new LatLng(3.3947997771, -76.539024176),
                new LatLng(3.48449473401, -76.508716889),
                new LatLng(3.43667949273, -76.546283307),
                new LatLng(3.40365230191, -76.524707217),
                new LatLng(3.42577154332, -76.517187875),
                new LatLng(3.48907264454, -76.519586273)
        };

        for (LatLng car: cars) {
            mMap.addMarker(
                    new MarkerOptions()
                            .position(car)
                            //.title("Mazda")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
            ).setTag(0);
        }*/

/*
        LatLng car1 = new LatLng(3.3947997771, -76.539024176);
        LatLng car2 = new LatLng(3.48449473401, -76.508716889);
        LatLng car3 = new LatLng(3.43667949273, -76.546283307);
        LatLng car4 = new LatLng(3.40365230191, -76.524707217);
        LatLng car5 = new LatLng(3.42577154332, -76.517187875);
        LatLng car6 = new LatLng(3.48907264454, -76.519586273);
        mMap.addMarker(
                new MarkerOptions()
                        .position(car1)
                        .title("Mazda")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
        ).setTag(0);
        mMap.addMarker(
                new MarkerOptions()
                        .position(car2)
                        .title("Chevrolet")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
        ).setTag(0);
        mMap.addMarker(
                new MarkerOptions()
                        .position(car3)
                        .title("Toyota")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
        ).setTag(0);
        mMap.addMarker(
                new MarkerOptions()
                        .position(car4)
                        .title("Ford")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
        ).setTag(0);
        mMap.addMarker(
                new MarkerOptions()
                        .position(car5)
                        .title("Fiat")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
        ).setTag(0);
        mMap.addMarker(
                new MarkerOptions()
                        .position(car6)
                        .title("Mustang")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_small))
        ).setTag(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(car5));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 12.0f ));

        // Se posiciona la cámara del mapa sobre un marker específico
        CameraPosition cameraPosition = CameraPosition
                .builder()
                .target(car1)
                .zoom(13)
                .bearing(90) // Rotar de forma circular 90 grados
                .build();

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Integer clickCount = (Integer) marker.getTag();

                // Check if a click count was set, then display the click count.
                if (clickCount != null) {
                    clickCount = clickCount + 1;
                    marker.setTag(clickCount);
                    Toast.makeText(MapsActivity.this,
                            marker.getTitle() +
                                    " has been clicked " + clickCount + " times.",
                            Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(
                        MapsActivity.this,
                        StreetViewActivity.class
                );
                intent.putExtra("latitud", marker.getPosition().latitude+"");
                intent.putExtra("longitud",marker.getPosition().longitude+"");
                startActivity(intent);

                return false;
            }
        });

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions
                .width(3)
                .color(Color.BLUE)
                .add(car1)
                .add(car2);

        mMap.addPolyline(polylineOptions);
        mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                5000,
                null
        );

        // polylineOptions = null;
        IGoogleMaps iGoogleMaps = new GoogleMaps();
        try {
            RutaModel rutaDAO = iGoogleMaps.obtenerRuta(car1, car2);
            cargarPolylines(rutaDAO, mMap, Color.GRAY);
            rutaDAO = iGoogleMaps.obtenerRuta(car1, car3);
            cargarPolylines(rutaDAO, mMap, Color.YELLOW);
            rutaDAO = iGoogleMaps.obtenerRuta(car1, car4);
            cargarPolylines(rutaDAO, mMap, Color.CYAN);
            rutaDAO = iGoogleMaps.obtenerRuta(car1, car5);
            cargarPolylines(rutaDAO, mMap, Color.BLACK);
            rutaDAO = iGoogleMaps.obtenerRuta(car1, car6);
            cargarPolylines(rutaDAO, mMap, Color.MAGENTA);
            rutaDAO = iGoogleMaps.obtenerRuta(car2, car3);
            cargarPolylines(rutaDAO, mMap, Color.YELLOW);
            rutaDAO = iGoogleMaps.obtenerRuta(car2, car4);
            cargarPolylines(rutaDAO, mMap, Color.CYAN);
            rutaDAO = iGoogleMaps.obtenerRuta(car2, car5);
            cargarPolylines(rutaDAO, mMap, Color.BLACK);
            rutaDAO = iGoogleMaps.obtenerRuta(car2, car6);
            cargarPolylines(rutaDAO, mMap, Color.MAGENTA);
            rutaDAO = iGoogleMaps.obtenerRuta(car3, car4);
            cargarPolylines(rutaDAO, mMap, Color.GREEN);
            rutaDAO = iGoogleMaps.obtenerRuta(car3, car5);
            cargarPolylines(rutaDAO, mMap, Color.BLACK);
            rutaDAO = iGoogleMaps.obtenerRuta(car3, car6);
            cargarPolylines(rutaDAO, mMap, Color.MAGENTA);
            rutaDAO = iGoogleMaps.obtenerRuta(car4, car5);
            cargarPolylines(rutaDAO, mMap, Color.BLACK);
            rutaDAO = iGoogleMaps.obtenerRuta(car4, car6);
            cargarPolylines(rutaDAO, mMap, Color.MAGENTA);
            rutaDAO = iGoogleMaps.obtenerRuta(car5, car6);
            cargarPolylines(rutaDAO, mMap, Color.RED);

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    public void mostrarMensaje(String mensaje, Integer largo) {
        Toast.makeText(
                MapsActivity.this,
                mensaje,
                largo==1?Toast.LENGTH_LONG:Toast.LENGTH_SHORT
        ).show();
    }

    private void cargarPolylines(RutaModel rutaModel, GoogleMap mMap, int color) {
        PolylineOptions polylineOptions = new PolylineOptions();
        try {

            if (rutaModel !=null && rutaModel.getlPuntos()!=null && rutaModel.getlPuntos().size()>0) {
                List<LatLng> puntosPintar = rutaModel.getlPuntos();

                //if (polylineOptions==null) {
                // Se evita que vuelva a crear las rutas
                polylineOptions = new PolylineOptions();
                //}

                polylineOptions.width(3).color(color);

                for (int x=0;x<puntosPintar.size();x++) {
                    polylineOptions.add(puntosPintar.get(x));
                }

                mMap.addPolyline(polylineOptions);
            }
        } catch (Exception ex) {

        }
    }

    private void cargarSpinner() {
        lVehiculos = new ArrayList<String>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");

        /*
         * Con Query se hace consulta por un campo específico dentro de la tabla.
         * Si hubiera un child sería,
         * databaseReference.child("nombreChild").orderByChild("idUsuario").equalTo(userID);
         */
        Query query = databaseReference.orderByChild("idUsuario").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mostrarMensaje(
                            getResources().getString(R.string.update_car_list),
                            1
                    );
                    lVehiculos.clear();
                    lVehiculos.add(getResources().getString(R.string.choose_car));
                    try {
                        for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                            if (vehiculo.getActivo().equals("1")) {
                                lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("MainActivity", "402) ERROR: "+ex.getMessage());
                    }

                    ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                            MapsActivity.this,
                            android.R.layout.simple_spinner_item,
                            lVehiculos
                    );
                    adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spVehiculosMap.setAdapter(adapterVehiculo);

                    // Cuando se seleccione un elemento, quede mostrándose en el spinner
                    spVehiculosMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            spVehiculosMap.setSelection(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void cargarSpinner2() {
        lVehiculos = new ArrayList<String>();
        FirebaseAuth firebaseAuth;
        FirebaseUser firebaseUser;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");

        ICarrosController iCarrosController = new CarrosController();
        lVehiculos.add("Todos los vehículos...");
        try {
            lVehiculos = iCarrosController.listarVehiculos(firebaseUser.getUid().toString());
        } catch (Exception e) {
            mostrarMensaje(
                    "ERROR: "+e.getMessage(),
                    1
            );
        }

        ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                MapsActivity.this,
                android.R.layout.simple_spinner_item,
                lVehiculos
        );
        adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spVehiculosMap.setAdapter(adapterVehiculo);

        // Cuando se seleccione un elemento, quede mostrándose en el spinner
        spVehiculosMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spVehiculosMap.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mostrarMensaje(
                "Se actualizó lista de vehículos",
                1
        );

        /*
         * Con Query se hace consulta por un campo específico dentro de la tabla.
         * Si hubiera un child sería,
         * databaseReference.child("nombreChild").orderByChild("idUsuario").equalTo(userID);
         *
        Query query = databaseReference.orderByChild("idUsuario").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(
                            MapsActivity.this,
                            "Se actualizó lista de vehículos",
                            Toast.LENGTH_LONG
                    ).show();

                    lVehiculos.clear();
                    lVehiculos.add("Todos los vehículos...");
                    try {
                        for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                            if (vehiculo.getActivo().equals("1")) {
                                lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("MainActivity", "214) ERROR: "+ex.getMessage());
                    }

                    ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                            MapsActivity.this,
                            android.R.layout.simple_spinner_item,
                            lVehiculos
                    );
                    adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spVehiculosMap.setAdapter(adapterVehiculo);

                    // Cuando se seleccione un elemento, quede mostrándose en el spinner
                    spVehiculosMap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            spVehiculosMap.setSelection(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public List<String> listarVehiculos(String idUsuario) throws Exception {
        return null;
    }
}
