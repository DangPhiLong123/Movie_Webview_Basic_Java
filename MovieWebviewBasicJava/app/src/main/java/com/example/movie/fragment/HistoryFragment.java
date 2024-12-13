package com.example.movie.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie.MyApplication;
import com.example.movie.R;
import com.example.movie.activity.PlayMovieActivity;
import com.example.movie.adapter.MovieHistoryAdapter;
import com.example.movie.model.Movie;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    // Danh sách để lưu các đối tượng phim được lấy từ Firebase
    private List<Movie> listMovies;
    // Adapter để kết nối danh sách phim với RecyclerView
    private MovieHistoryAdapter movieHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate (khởi tạo giao diện) cho fragment này
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Tìm RecyclerView trong giao diện
        RecyclerView rcvHistory = view.findViewById(R.id.rcv_history);

        // Đặt GridLayoutManager với 3 cột cho RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rcvHistory.setLayoutManager(gridLayoutManager);

        // Khởi tạo listMovies như một ArrayList mới
        listMovies = new ArrayList<>();

        // Khởi tạo movieHistoryAdapter với danh sách phim và đặt các lắng nghe sự kiện click
        movieHistoryAdapter = new MovieHistoryAdapter(listMovies, getActivity(),
                new MovieHistoryAdapter.IClickItemListener() {
                    @Override
                    public void onClickItem(Movie movie) {
                        // Xử lý khi nhấn vào một phim
                        onClickItemMovie(movie);
                    }

                    @Override
                    public void onClickFavorite(int id, boolean favorite) {
                        // Xử lý khi nhấn vào nút yêu thích (favorite)
                        onClickFavoriteMovie(id, favorite);
                    }
                });
        // Đặt adapter cho RecyclerView
        rcvHistory.setAdapter(movieHistoryAdapter);

        // Lấy danh sách các phim đã xem (trong lịch sử)
        getListMoviesHistory();

        return view; // Trả về giao diện đã khởi tạo
    }

    // Hàm để lấy danh sách phim từ lịch sử xem
    private void getListMoviesHistory() {
        // Kiểm tra xem activity có tồn tại hay không
        if (getActivity() == null) {
            return;
        }
        // Truy vấn Firebase để lấy các phim có thuộc tính "history" = true
        MyApplication.get(getActivity()).getDatabaseReference().orderByChild("history").equalTo(true)
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Lấy đối tượng Movie từ dataSnapshot
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || movieHistoryAdapter == null) {
                            return; // Trả về nếu không lấy được movie hoặc list/adapter chưa được khởi tạo
                        }
                        // Thêm phim vào đầu danh sách
                        listMovies.add(0, movie);
                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        movieHistoryAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Lấy đối tượng Movie đã thay đổi từ dataSnapshot
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieHistoryAdapter == null) {
                            return; // Trả về nếu không lấy được movie hoặc list/adapter chưa được khởi tạo
                        }
                        // Cập nhật thông tin phim trong danh sách
                        for (Movie movieEntity : listMovies) {
                            if (movie.getId() == movieEntity.getId()) {
                                if (!movie.isHistory()) {
                                    // Xóa phim nếu không còn trong lịch sử
                                    listMovies.remove(movieEntity);
                                } else {
                                    // Cập nhật lại thông tin của phim nếu vẫn trong lịch sử
                                    movieEntity.setImage(movie.getImage());
                                    movieEntity.setTitle(movie.getTitle());
                                    movieEntity.setUrl(movie.getUrl());
                                    movieEntity.setFavorite(movie.isFavorite());
                                }
                                break; // Thoát khỏi vòng lặp sau khi cập nhật
                            }
                        }
                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        movieHistoryAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // Lấy đối tượng Movie đã bị xóa từ dataSnapshot
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieHistoryAdapter == null) {
                            return; // Trả về nếu không lấy được movie hoặc list/adapter chưa được khởi tạo
                        }
                        // Xóa phim khỏi danh sách
                        for (Movie movieDelete : listMovies) {
                            if (movie.getId() == movieDelete.getId()) {
                                listMovies.remove(movieDelete);
                                break; // Thoát khỏi vòng lặp sau khi xóa
                            }
                        }
                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        movieHistoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Không sử dụng trong phần này
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý nếu có lỗi xảy ra trong quá trình truy vấn Firebase
                    }
                });
    }

    // Hàm xử lý khi người dùng nhấn vào một phim trong danh sách
    private void onClickItemMovie(Movie movie) {
        // Khởi tạo intent để mở PlayMovieActivity khi phim được nhấn
        Intent intent = new Intent(getActivity(), PlayMovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_movie", movie); // Đưa đối tượng phim vào bundle
        intent.putExtras(bundle); // Đính kèm bundle vào intent
        startActivity(intent); // Mở activity PlayMovieActivity
    }

    // Hàm xử lý khi người dùng nhấn vào nút yêu thích (favorite)
    private void onClickFavoriteMovie(int id, boolean favorite) {
        // Kiểm tra xem activity có tồn tại hay không
        if (getActivity() == null) {
            return;
        }
        // Tạo một map để cập nhật trạng thái yêu thích của phim trong Firebase
        Map<String, Object> map = new HashMap<>();
        map.put("favorite", favorite);
        MyApplication.get(getActivity()).getDatabaseReference()
                .child(String.valueOf(id)).updateChildren(map); // Cập nhật trạng thái yêu thích trong Firebase
    }

    // Hàm này sẽ được gọi khi fragment bị hủy
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Giải phóng tài nguyên khi fragment bị hủy
        if (movieHistoryAdapter != null) {
            movieHistoryAdapter.release();
        }
    }
}

