package com.marktony.zhuanlan.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.marktony.zhuanlan.R;
import com.marktony.zhuanlan.adapter.ZhuanlanAdapter;
import com.marktony.zhuanlan.app.VolleySingleton;
import com.marktony.zhuanlan.bean.Zhuanlan;
import com.marktony.zhuanlan.head.RentalsSunHeaderView;
import com.marktony.zhuanlan.interfaze.OnRecyclerViewOnClickListener;
import com.marktony.zhuanlan.utils.API;
import com.marktony.zhuanlan.utils.LocalDisplay;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Lizhaotailang on 2016/10/3.
 */

public class GlobalFragment extends Fragment {

    private int type;
    private RecyclerView recyclerView;
    private String[] ids;
    private ZhuanlanAdapter adapter;
    private ArrayList<Zhuanlan> list = new ArrayList<>();

    private Gson gson = new Gson();

    public static final int TYPE_PRODUCT = 0;
    public static final int TYPE_MUSIC = 1;
    public static final int TYPE_LIFE = 2;
    public static final int TYPE_EMOTION = 3;
    public static final int TYPE_FINANCE = 4;
    public static final int TYPE_ZHIHU = 5;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GlobalFragment() {

    }

    public static GlobalFragment newInstance() {
        return new GlobalFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universal,container,false);
     recyclerView= (RecyclerView) view.findViewById(R.id.rv_main);
        initViews(view);

//        refreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setRefreshing(true);
//            }
//        });

        switch (type){
            default:
            case TYPE_PRODUCT:
                ids = getActivity().getResources().getStringArray(R.array.product);
                break;
            case TYPE_MUSIC:
                ids = getActivity().getResources().getStringArray(R.array.music);
                break;
            case TYPE_LIFE:
                ids = getActivity().getResources().getStringArray(R.array.life);
                break;
            case TYPE_EMOTION:
                ids = getActivity().getResources().getStringArray(R.array.emotion);
                break;
            case TYPE_FINANCE:
                ids = getActivity().getResources().getStringArray(R.array.profession);
                break;
            case TYPE_ZHIHU:
                ids = getActivity().getResources().getStringArray(R.array.zhihu);
                break;

        }

        for (int i = 0;i < ids.length; i++) {

            final int finalI = i;

            StringRequest request = new StringRequest(Request.Method.GET, API.BASE_URL + ids[i], new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Zhuanlan z = gson.fromJson(s, Zhuanlan.class);
                    list.add(z);

                    if (adapter == null) {
                        adapter = new ZhuanlanAdapter(getActivity(),list);
                        recyclerView.setAdapter(adapter);
                        adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                            @Override
                            public void OnClick(View v, int position) {
                                Intent intent = new Intent(getContext(),PostsListActivity.class);
                                intent.putExtra("slug",list.get(position).getSlug());
                                intent.putExtra("title",list.get(position).getName());
                                intent.putExtra("post_count", list.get(position).getPostsCount());
                                startActivity(intent);
                            }
                        });
                    } else {
                        adapter.notifyItemInserted(list.size() - 1);
                    }

//                    if (finalI == (ids.length - 1)){
//
//                        refreshLayout.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                refreshLayout.setRefreshing(false);
//                            }
//                        });
//
//                        refreshLayout.setEnabled(false);
//                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });

            VolleySingleton.getVolleySingleton(getActivity()).addToRequestQueue(request);

        }

        return view;
    }

    private void initViews(View view) {

        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final RentalsSunHeaderView header = new RentalsSunHeaderView(getContext());
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

}
