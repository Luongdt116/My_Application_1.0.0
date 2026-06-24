package huce.fit.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import huce.fit.myapplication.R;
import huce.fit.myapplication.objects.Service;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private List<String> serviceIds;
    private Map<String, Integer> selectedQuantities = new HashMap<>();
    private OnServiceQuantityChangeListener listener;

    public interface OnServiceQuantityChangeListener {
        void onQuantityChanged(Map<String, Integer> selectedServices);
    }

    public ServiceAdapter(Map<String, Service> services, OnServiceQuantityChangeListener listener) {
        this.serviceList = new ArrayList<>(services.values());
        this.serviceIds = new ArrayList<>(services.keySet());
        this.listener = listener;
        for (String id : serviceIds) {
            selectedQuantities.put(id, 0);
        }
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_selection, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        String serviceId = serviceIds.get(position);
        int quantity = selectedQuantities.get(serviceId);

        holder.tvName.setText(service.getName());
        holder.tvPrice.setText(String.format("%,dđ / %s", service.getPrice(), service.getUnit()));
        holder.tvQuantity.setText(String.valueOf(quantity));

        holder.btnPlus.setOnClickListener(v -> {
            int newQty = selectedQuantities.get(serviceId) + 1;
            selectedQuantities.put(serviceId, newQty);
            holder.tvQuantity.setText(String.valueOf(newQty));
            if (listener != null) listener.onQuantityChanged(selectedQuantities);
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = selectedQuantities.get(serviceId);
            if (currentQty > 0) {
                int newQty = currentQty - 1;
                selectedQuantities.put(serviceId, newQty);
                holder.tvQuantity.setText(String.valueOf(newQty));
                if (listener != null) listener.onQuantityChanged(selectedQuantities);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public Map<String, Integer> getSelectedServices() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : selectedQuantities.entrySet()) {
            if (entry.getValue() > 0) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView btnMinus, btnPlus;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
