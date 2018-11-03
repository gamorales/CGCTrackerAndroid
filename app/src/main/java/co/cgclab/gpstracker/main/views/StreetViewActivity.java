package co.cgclab.gpstracker.main.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import co.cgclab.gpstracker.R;

public class StreetViewActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama streetViewPanorama;
    private LatLng ubicacion;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_street_view);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                                                    .findFragmentById(R.id.street_view_panorama);

        Intent intent = getIntent();
        // Cuando el api de google está lista, sincroniza el fragment con el street view
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        ubicacion = new LatLng(
            new Double(intent.getStringExtra("latitud")),
            new Double(intent.getStringExtra("longitud"))
        );
    }

    // Se ejecuta al final, cuando google ya cargó to-do lo referente al street view
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama;

        if (ubicacion!=null) {
            this.streetViewPanorama.setPosition(ubicacion);
        }
    }
}
