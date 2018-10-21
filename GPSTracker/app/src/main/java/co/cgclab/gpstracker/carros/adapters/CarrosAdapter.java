package co.cgclab.gpstracker.carros.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.carros.models.CarrosModel;

public class CarrosAdapter extends BaseAdapter {
    private static LayoutInflater layoutInflater = null;
    Context context;
    List<CarrosModel> carrosModel;

    public CarrosAdapter(Context context, List<CarrosModel> carrosModel) {
        this.context = context;
        this.carrosModel = carrosModel;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return carrosModel.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View vista = layoutInflater.inflate(R.layout.activity_carros_content_fields, null);
        TextView lblCarroPlaca = vista.findViewById(R.id.lblCarroPlaca);
        TextView lblCarroNombre = vista.findViewById(R.id.lblCarroNombre);
        TextView lblCarroTelefono = vista.findViewById(R.id.lblCarroTelefono);
        TextView lblCarroEstado = vista.findViewById(R.id.lblCarroEstado);

        lblCarroPlaca.setText(carrosModel.get(i).getPlaca());
        lblCarroNombre.setText(carrosModel.get(i).getNombre());
        lblCarroTelefono.setText(carrosModel.get(i).getTelefono());
        lblCarroEstado.setText(carrosModel.get(i).getActivo().equals("1")?"Activo":"Inactivo");

        return vista;
    }
}
