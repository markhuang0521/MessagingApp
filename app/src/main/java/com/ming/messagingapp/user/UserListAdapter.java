package com.ming.messagingapp.user;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.ming.messagingapp.R;

        import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<User> userList;
    final private UserListAdapterOnClickHandler onClickHandler;

    public interface UserListAdapterOnClickHandler {
        void onClick(User user);
    }

    public UserListAdapter(List<User> userList, UserListAdapterOnClickHandler onClickHandler) {
        this.userList = userList;
        this.onClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = userList.get(position).getName();
        String phone = userList.get(position).getPhoneNumber();
        holder.name.setText(name);
        holder.phoneNumber.setText(phone);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, phoneNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.tv_user_name);
            phoneNumber = itemView.findViewById(R.id.tv_user_phone_number);
        }

        @Override
        public void onClick(View view) {
            User user = userList.get(getAdapterPosition());
            onClickHandler.onClick(user);
        }
    }
}
