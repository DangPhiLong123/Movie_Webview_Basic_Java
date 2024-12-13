package com.example.movie.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.example.movie.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    // Danh sách chứa các bộ phim và adapter để kết nối dữ liệu với RecyclerView
    private List<Movie> listMovies;
    private MovieAdapter movieAdapter;
    private EditText edtSearchName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment từ file XML
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Liên kết các thành phần trong giao diện với mã nguồn
        RecyclerView rcvHome = view.findViewById(R.id.rcv_home);
        edtSearchName = view.findViewById(R.id.edt_search_name);
        ImageView imgSearch = view.findViewById(R.id.img_search);

        // Cấu hình layout cho RecyclerView, hiển thị 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvHome.setLayoutManager(gridLayoutManager);

        // Khởi tạo danh sách phim và adapter để kết nối dữ liệu với RecyclerView
        listMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(listMovies, getActivity(), new MovieAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Movie movie) {
                // Xử lý sự kiện khi người dùng nhấn vào một bộ phim
                onClickItemMovie(movie);
            }

            @Override
            public void onClickFavorite(int id, boolean favorite) {
                // Xử lý sự kiện khi người dùng nhấn vào nút yêu thích
                onClickFavoriteMovie(id, favorite);
            }
        });
        rcvHome.setAdapter(movieAdapter);

        // Lấy danh sách phim từ Firebase
        getListMovies("");

        // Xử lý sự kiện khi người dùng nhấn vào nút tìm kiếm
        imgSearch.setOnClickListener(view1 -> searchMovie());

        // Xử lý sự kiện khi người dùng nhấn nút tìm kiếm trên bàn phím (IME_ACTION_SEARCH)
        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovie();
                return true;
            }
            return false;
        });

        // Xử lý sự kiện thay đổi văn bản trong ô tìm kiếm
        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không làm gì trước khi thay đổi văn bản
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không làm gì khi văn bản đang thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Sau khi văn bản thay đổi, nếu ô tìm kiếm trống, tải lại danh sách phim
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    listMovies.clear();
                    getListMovies("");
                }
            }
        });

        return view;
    }

    // Phương thức tìm kiếm phim dựa trên từ khóa
    private void searchMovie() {
        String strKey = edtSearchName.getText().toString().trim();
        listMovies.clear();
        getListMovies(strKey);
        Utils.hideSoftKeyboard(getActivity()); // Ẩn bàn phím sau khi tìm kiếm
    }

    // Phương thức lấy danh sách phim từ Firebase Realtime Database
    private void getListMovies(String key) {
        if (getActivity() == null) {
            return;
        }
        // Truy cập Firebase Realtime Database thông qua MyApplication
        MyApplication.get(getActivity()).getDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Khi một bộ phim mới được thêm vào Firebase, dữ liệu sẽ được lấy và thêm vào danh sách
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || movieAdapter == null) {
                            return;
                        }

                        // Nếu không có từ khóa tìm kiếm, thêm tất cả các phim
                        if (key == null || key.equals("")) {
                            listMovies.add(0, movie);
                        } else {
                            // Nếu có từ khóa, chỉ thêm các phim có tên chứa từ khóa
                            if (movie.getTitle().toLowerCase().trim().contains(key.toLowerCase().trim())) {
                                listMovies.add(0, movie);
                            }
                        }

                        // Cập nhật lại RecyclerView sau khi dữ liệu thay đổi
                        movieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Khi dữ liệu của một bộ phim thay đổi, cập nhật danh sách và RecyclerView
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieAdapter == null) {
                            return;
                        }
                        // Tìm bộ phim trong danh sách và cập nhật thông tin mới
                        for (Movie movieEntity : listMovies) {
                            if (movie.getId() == movieEntity.getId()) {
                                movieEntity.setImage(movie.getImage());
                                movieEntity.setTitle(movie.getTitle());
                                movieEntity.setUrl(movie.getUrl());
                                movieEntity.setFavorite(movie.isFavorite());
                                movieEntity.setHistory(movie.isHistory());
                                break;
                            }
                        }
                        movieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // Khi một bộ phim bị xóa khỏi Firebase, xóa nó khỏi danh sách và cập nhật RecyclerView
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieAdapter == null) {
                            return;
                        }
                        for (Movie movieDelete : listMovies) {
                            if (movie.getId() == movieDelete.getId()) {
                                listMovies.remove(movieDelete);
                                break;
                            }
                        }
                        movieAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Không cần xử lý sự kiện này
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi khi truy cập Firebase thất bại
                    }
                });
    }

    // Xử lý sự kiện khi người dùng nhấn vào một bộ phim để xem
    private void onClickItemMovie(Movie movie) {
        // Tạo intent để mở PlayMovieActivity và truyền dữ liệu của bộ phim đã chọn
        Intent intent = new Intent(getActivity(), PlayMovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_movie", movie); // Truyền đối tượng Movie
        intent.putExtras(bundle);
        startActivity(intent); // Bắt đầu PlayMovieActivity
    }

    // Xử lý sự kiện khi người dùng nhấn vào nút yêu thích của một bộ phim
    private void onClickFavoriteMovie(int id, boolean favorite) {
        if (getActivity() == null) {
            return;
        }
        // Tạo một Map để cập nhật thuộc tính "favorite" của phim trong Firebase
        Map<String, Object> map = new HashMap<>();
        map.put("favorite", favorite);
        MyApplication.get(getActivity()).getDatabaseReference()
                .child(String.valueOf(id)).updateChildren(map); // Cập nhật trạng thái yêu thích
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (movieAdapter != null) {
            movieAdapter.release(); // Giải phóng tài nguyên của adapter khi fragment bị hủy
        }
    }
}

