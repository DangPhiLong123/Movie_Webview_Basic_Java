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
import com.example.movie.adapter.MovieAdapter;
import com.example.movie.model.Movie;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    // Danh sách lưu các bộ phim được yêu thích
    private List<Movie> listMovies;
    // Adapter để kết nối danh sách phim với RecyclerView
    private MovieAdapter movieAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo giao diện cho fragment này
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Tìm RecyclerView trong giao diện
        RecyclerView rcvFavorite = view.findViewById(R.id.rcv_favorite);

        // Đặt GridLayoutManager với 2 cột cho RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvFavorite.setLayoutManager(gridLayoutManager);

        // Khởi tạo listMovies như một ArrayList
        listMovies = new ArrayList<>();

        // Khởi tạo movieAdapter với danh sách phim và các sự kiện click
        movieAdapter = new MovieAdapter(listMovies, getActivity(), new MovieAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Movie movie) {
                // Xử lý khi nhấn vào một bộ phim
                onClickItemMovie(movie);
            }

            @Override
            public void onClickFavorite(int id, boolean favorite) {
                // Xử lý khi nhấn vào nút yêu thích
                onClickFavoriteMovie(id, favorite);
            }
        });

        // Đặt adapter cho RecyclerView
        rcvFavorite.setAdapter(movieAdapter);

        // Gọi hàm để lấy danh sách các bộ phim yêu thích
        getListMoviesFavorite();

        return view; // Trả về giao diện đã khởi tạo
    }

    // Hàm lấy danh sách các bộ phim được yêu thích từ Firebase
    private void getListMoviesFavorite() {
        // Kiểm tra xem activity có tồn tại không
        if (getActivity() == null) {
            return;
        }
        // Truy vấn Firebase để lấy các phim có thuộc tính "favorite" = true
        MyApplication.get(getActivity()).getDatabaseReference().orderByChild("favorite").equalTo(true)
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Lấy đối tượng Movie từ dataSnapshot
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || movieAdapter == null) {
                            return; // Trả về nếu movie, list hoặc adapter chưa được khởi tạo
                        }
                        // Thêm bộ phim vào đầu danh sách
                        listMovies.add(0, movie);
                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        movieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Lấy đối tượng Movie đã thay đổi từ dataSnapshot
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieAdapter == null) {
                            return; // Trả về nếu movie hoặc list/adapter chưa được khởi tạo
                        }
                        // Cập nhật thông tin bộ phim trong danh sách nếu cần thiết
                        for (Movie movieEntity : listMovies) {
                            if (movie.getId() == movieEntity.getId()) {
                                if (!movie.isFavorite()) {
                                    // Xóa bộ phim nếu không còn là phim yêu thích
                                    listMovies.remove(movieEntity);
                                } else {
                                    // Cập nhật thông tin của bộ phim
                                    movieEntity.setImage(movie.getImage());
                                    movieEntity.setTitle(movie.getTitle());
                                    movieEntity.setUrl(movie.getUrl());
                                    movieEntity.setHistory(movie.isHistory());
                                }
                                break; // Thoát khỏi vòng lặp sau khi cập nhật
                            }
                        }
                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        movieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // Lấy đối tượng Movie đã bị xóa từ dataSnapshot
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieAdapter == null) {
                            return; // Trả về nếu movie hoặc list/adapter chưa được khởi tạo
                        }
                        // Xóa bộ phim khỏi danh sách
                        for (Movie movieDelete : listMovies) {
                            if (movie.getId() == movieDelete.getId()) {
                                listMovies.remove(movieDelete);
                                break; // Thoát khỏi vòng lặp sau khi xóa
                            }
                        }
                        // Thông báo cho adapter về sự thay đổi dữ liệu
                        movieAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Không sử dụng trong phần này
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý khi có lỗi truy vấn Firebase
                    }
                });
    }

    // Hàm xử lý khi người dùng nhấn vào một bộ phim
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
        // Kiểm tra xem activity có tồn tại không
        if (getActivity() == null) {
            return;
        }
        // Tạo một map để cập nhật trạng thái yêu thích của phim trong Firebase
        Map<String, Object> map = new HashMap<>();
        map.put("favorite", favorite);
        MyApplication.get(getActivity()).getDatabaseReference()
                .child(String.valueOf(id)).updateChildren(map); // Cập nhật trạng thái yêu thích trong Firebase
    }

    // Giải phóng tài nguyên khi fragment bị hủy
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (movieAdapter != null) {
            movieAdapter.release();
        }
    }
}
