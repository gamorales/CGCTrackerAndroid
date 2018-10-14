package co.cgclab.gpstracker.main.controllers;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class ClusterRenderer extends DefaultClusterRenderer<Markers> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<Markers> clusterManager) {
        super(context, map, clusterManager);
        clusterManager.setRenderer(this);
    }

    @Override
    protected void onBeforeClusterItemRendered(Markers markerItem, MarkerOptions markerOptions) {
        if (markerItem.getIcon() != null) {
            markerOptions.icon(markerItem.getIcon()); //Here you retrieve BitmapDescriptor from ClusterItem and set it as marker icon
        }
        markerOptions.visible(true);
    }
}