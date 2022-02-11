package io.agora.uiwidget.function;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.agora.uiwidget.databinding.EditFaceSelectViewBinding;
import io.agora.uiwidget.function.editface.tabs.ColorItemTab;
import io.agora.uiwidget.function.editface.tabs.DecorationTab;
import io.agora.uiwidget.function.editface.tabs.GlassesTab;
import io.agora.uiwidget.function.editface.tabs.ImageTab;
import io.agora.uiwidget.function.editface.tabs.ItemTab;
import io.agora.uiwidget.function.editface.tabs.MakeUpTab;
import io.agora.uiwidget.function.editface.tabs.ShapeTab;
import io.agora.uiwidget.function.editface.tabs.Tab;

public class EditFaceSelectView extends FrameLayout {

    private EditFaceSelectViewBinding mViewBinding;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mVPAdapter;
    private final List<Tab> vpTabs = new ArrayList<>();

    public EditFaceSelectView(@NonNull Context context) {
        this(context, null);
    }

    public EditFaceSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditFaceSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewBinding = EditFaceSelectViewBinding.inflate(LayoutInflater.from(getContext()), this, true);

        mVPAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return createVPViewHolder(parent, viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                vpTabs.get(position).onBindViewHolder(holder, position);
            }

            @Override
            public int getItemCount() {
                return vpTabs.size();
            }

            @Override
            public int getItemViewType(int position) {
                return vpTabs.get(position).viewType;
            }

        };


        mViewBinding.viewPager2.setAdapter(mVPAdapter);

        mViewBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mViewBinding.tabLayout.selectTab(mViewBinding.tabLayout.getTabAt(position), true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        mViewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewBinding.viewPager2.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @NonNull
    private RecyclerView.ViewHolder createVPViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Tab.VIEW_TYPE_IMAGE:
                return new ImageTab.ImageTabViewHolder(parent);
            case Tab.VIEW_TYPE_COLOR_ITEM:
                return new ColorItemTab.ColorItemTabViewHolder(parent);
            case Tab.VIEW_TYPE_SHAPE:
                return new ShapeTab.ShapeTabViewHolder(parent);
            case Tab.VIEW_TYPE_ITEM:
                return new ItemTab.ItemTabViewHolder(parent);
            case Tab.VIEW_TYPE_MAKE_UP:
                return new MakeUpTab.MakeUpTabViewHolder(parent);
            case Tab.VIEW_TYPE_DECORATION:
                return new DecorationTab.DecorationTabViewHolder(parent);
            case Tab.VIEW_TYPE_GLASSES:
                return new GlassesTab.GlassesTabViewHolder(parent);
        }
        throw new IllegalArgumentException("cannot find view type: " + viewType);
    }


    public void addTab(Tab<?> tab) {
        TabLayout.Tab _tab = mViewBinding.tabLayout.newTab();
        _tab.setText(tab.title);
        mViewBinding.tabLayout.addTab(_tab);

        int insertIndex = vpTabs.size();
        vpTabs.add(tab);
        mVPAdapter.notifyItemInserted(insertIndex);
    }

}
