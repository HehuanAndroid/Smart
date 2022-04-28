package com.vcom.smart.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseMvvmFragment;
import com.vcom.smart.databinding.DialogGuideAddSceneBinding;
import com.vcom.smart.databinding.DialogGuideAddSceneSingleBinding;
import com.vcom.smart.databinding.FragmentSceneBinding;
import com.vcom.smart.fvm.SceneFVM;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Scene;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Lzz
 * @Date 2020/10/27 18:44
 */
public class SceneFragment extends BaseMvvmFragment<SceneFVM, FragmentSceneBinding> implements SceneFVM.SceneFvmCallBack {

    private AlertDialog alertDialog;
    private final List<Scene> guideScene = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene;
    }

    @Override
    protected void afterCreate() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
    }

    @Override
    protected void initDatum() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);
    }

    @Override
    public void goGuideScene() {

        if (getActivity() == null) {
            return;
        }

        guideScene.clear();

        Scene scene1 = new Scene("场景1","0");
        Scene scene2 = new Scene("场景2","0");
        Scene scene3 = new Scene("场景3","0");

        DialogGuideAddSceneBinding guideAddRegionBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_guide_add_scene, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(guideAddRegionBinding.getRoot())
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        guideAddRegionBinding.dialogGuideAddSceneCancel.setOnClickListener(v -> alertDialog.dismiss());

        guideAddRegionBinding.dialogGuideAddSceneOne.setOnClickListener(view -> {
            if (guideAddRegionBinding.dialogGuideAddSceneOneIcon.getVisibility() == View.GONE) {
                guideScene.add(scene1);
                guideAddRegionBinding.dialogGuideAddSceneOneIcon.setVisibility(View.VISIBLE);
            } else {
                guideScene.remove(scene1);
                guideAddRegionBinding.dialogGuideAddSceneOneIcon.setVisibility(View.GONE);
            }
        });

        guideAddRegionBinding.dialogGuideAddSceneTwo.setOnClickListener(view -> {
            if (guideAddRegionBinding.dialogGuideAddSceneTwoIcon.getVisibility() == View.GONE) {
                guideScene.add(scene2);
                guideAddRegionBinding.dialogGuideAddSceneTwoIcon.setVisibility(View.VISIBLE);
            } else {
                guideScene.remove(scene2);
                guideAddRegionBinding.dialogGuideAddSceneTwoIcon.setVisibility(View.GONE);
            }
        });

        guideAddRegionBinding.dialogGuideAddSceneThree.setOnClickListener(view -> {
            if (guideAddRegionBinding.dialogGuideAddSceneThreeIcon.getVisibility() == View.GONE) {
                guideScene.add(scene3);
                guideAddRegionBinding.dialogGuideAddSceneThreeIcon.setVisibility(View.VISIBLE);
            } else {
                guideScene.remove(scene3);
                guideAddRegionBinding.dialogGuideAddSceneThreeIcon.setVisibility(View.GONE);
            }
        });

        guideAddRegionBinding.dialogGuideAddSceneCommit.setOnClickListener(v -> alertDialog.dismiss());
    }

    @Override
    public void addScene() {
        if (getActivity() == null) {
            return;
        }

        DialogGuideAddSceneSingleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_guide_add_scene_single, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();
        alertDialog.setCancelable(false);

        Integer[] icons = {R.drawable.ic_scene_0, R.drawable.ic_scene_1, R.drawable.ic_scene_2};
        Integer[] iconSelected = {R.drawable.ic_scene_selected_0, R.drawable.ic_scene_selected_1, R.drawable.ic_scene_selected_2};

        SceneIconAdapter adapter = new SceneIconAdapter(getActivity(), icons, iconSelected);

        binding.dialogGuideAddSceneSingleCancel.setOnClickListener(v -> alertDialog.dismiss());
        binding.dialogGuideAddSceneSingleRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.dialogGuideAddSceneSingleRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.dialogGuideAddSceneSingleCommit.setOnClickListener(v -> {

        });

        alertDialog.show();
    }

    private static class SceneIconViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;

        public SceneIconViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_scene_icon_image);
        }
    }

    private static class SceneIconAdapter extends RecyclerView.Adapter<SceneIconViewHolder> {

        private final Integer[] data;
        private final Integer[] dataSelected;
        private final Context mContext;

        private int selectIndex = 0;

        public SceneIconAdapter(Context context, Integer[] arg0, Integer[] arg1) {
            mContext = context;
            this.data = arg0;
            this.dataSelected = arg1;
        }

        public void setSelectIndex(int selectIndex) {
            this.selectIndex = selectIndex;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SceneIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_scene_icon, parent, false);
            return new SceneIconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SceneIconViewHolder holder, int position) {
            if (selectIndex == position) {
                holder.icon.setBackgroundResource(dataSelected[position]);
            } else {
                holder.icon.setBackgroundResource(data[position]);
            }
            holder.itemView.setOnClickListener(v -> setSelectIndex(position));
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }
}
