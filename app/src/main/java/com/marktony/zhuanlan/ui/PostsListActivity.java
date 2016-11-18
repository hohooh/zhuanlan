package com.marktony.zhuanlan.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.marktony.zhuanlan.R;

import com.marktony.zhuanlan.adapter.PostsAdapter;
import com.marktony.zhuanlan.app.VolleySingleton;
import com.marktony.zhuanlan.bean.ZhuanlanListItem;
import com.marktony.zhuanlan.head.RentalsSunHeaderView;
import com.marktony.zhuanlan.utils.API;
import com.marktony.zhuanlan.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.zhuanlan.utils.LocalDisplay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class PostsListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;


    private List<ZhuanlanListItem> list = new ArrayList<ZhuanlanListItem>();
    private PostsAdapter adapter;

    private Gson gson = new Gson();

    private String slug;
    private int postCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);
        recyclerView= (RecyclerView)findViewById(R.id.rv_post);
        initViews();



        Intent intent = getIntent();
        slug = intent.getStringExtra("slug");
        postCount = intent.getIntExtra("post_count", 0);
        String title = intent.getStringExtra("title");

        getSupportActionBar().setTitle(title);

        loadData();

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

                //当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的itemposition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    //判断是否滚动到底部并且是向下滑动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {

                        if (list.size() < postCount){
                            loadData();
                        } else {
                            Snackbar.make(toolbar, R.string.no_more,Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });

    }

    private void loadData() {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API.BASE_URL + slug + "/posts?limit=20&offset=" + list.size(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                for (int i = 0;i < jsonArray.length();i++){

                    try {
                        JSONObject object = jsonArray.getJSONObject(i);

                        ZhuanlanListItem item = gson.fromJson(object.toString(), ZhuanlanListItem.class);

                        list.add(item);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (adapter == null) {

                    adapter = new PostsAdapter(PostsListActivity.this,list);
                    recyclerView.setAdapter(adapter);

                    adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                        @Override
                        public void OnClick(View v, int position) {

                            Intent intent = new Intent(PostsListActivity.this,ZhuanlanPostDetailActivity.class);
                            intent.putExtra("img_url", list.get(position).getTitleImage());
                            intent.putExtra("title",list.get(position).getTitle());
                            intent.putExtra("slug",list.get(position).getSlug());

                            startActivity(intent);

                        }
                    });


                } else {
                    adapter.notifyItemInserted(list.size());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        VolleySingleton.getVolleySingleton(this).addToRequestQueue(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager manager = new LinearLayoutManager(PostsListActivity.this);
        recyclerView.setLayoutManager(manager);
        final PtrFrameLayout frame = (PtrFrameLayout) findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostsListActivity.this));
        final RentalsSunHeaderView header = new RentalsSunHeaderView(this);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setUp(frame);

        frame.setLoadingMinTime(1000);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        // frame.setPullToRefresh(true);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(true);
            }
        }, 500);
        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                /**
                 * 如果 Content 不是 ViewGroup，返回 true,表示可以下拉</br>
                 * 例如：TextView，ImageView
                 */
                if (!(content instanceof ViewGroup)) {
                    return true;
                }
                ViewGroup viewGroup = (ViewGroup) content;
                /**
                 * 如果 Content 没有子 View（内容为空）时候，返回 true，表示可以下拉
                 */
                if (viewGroup.getChildCount() == 0) {
                    return true;
                }
                /**
                 * 如果 Content 是 AbsListView（ListView，GridView），当第一个 item 不可见是，返回 false，不可以下拉。
                 */
                if (viewGroup instanceof AbsListView) {
                    AbsListView listView = (AbsListView) viewGroup;
                    if (listView.getFirstVisiblePosition() > 0) {
                        return false;
                    }
                }
                /**
                 * 最终判断，判断第一个子 View 的 top 值</br>
                 * 如果第一个子 View 有 margin，则当 top==子 view 的 marginTop+content 的 paddingTop 时，表示在最顶部，返回 true，可以下拉</br>
                 * 如果没有 margin，则当 top==content 的 paddinTop 时，表示在最顶部，返回 true，可以下拉
                 */
                View child = viewGroup.getChildAt(0);
                ViewGroup.LayoutParams glp = child.getLayoutParams();
                int top = child.getTop();
                if (glp instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) glp;
                    return top == mlp.topMargin + viewGroup.getPaddingTop();
                } else {
                    return top == viewGroup.getPaddingTop();
                }

            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                long delay = 1500;
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, delay);

            }
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}