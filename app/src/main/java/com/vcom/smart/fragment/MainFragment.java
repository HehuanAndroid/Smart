package com.vcom.smart.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.vcom.smart.R;
import com.vcom.smart.base.BaseBindingAdapter;
import com.vcom.smart.base.BaseMvvmFragment;
import com.vcom.smart.databinding.DialogAddMenuBinding;
import com.vcom.smart.databinding.DialogAddRegionBinding;
import com.vcom.smart.databinding.DialogGuideAddRegionBinding;
import com.vcom.smart.databinding.DialogGuideAddSceneBinding;
import com.vcom.smart.databinding.DialogGuideAddSceneSingleBinding;
import com.vcom.smart.databinding.FragmentMainBinding;
import com.vcom.smart.databinding.ItemMainSceneBinding;
import com.vcom.smart.fvm.MainFVM;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.Scene;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.ui.AddSceneActivity;
import com.vcom.smart.ui.EditRegionActivity;
import com.vcom.smart.ui.SceneManagerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Lzz
 * @Date 2020/10/27 18:44
 */
public class MainFragment extends BaseMvvmFragment<MainFVM, FragmentMainBinding> implements MainFVM.MainFvmCallBack {

    private final List<Region> mRegions = new ArrayList<>();
    private final List<Scene> mScenes = new ArrayList<>();

    private AlertDialog alertDialog;
    private MainFragmentSceneAdapter sceneAdapter;

    private final List<Region> guideRegion = new ArrayList<>();
    private final List<Scene> guideScene = new ArrayList<>();

    private final AtomicInteger regionIndex = new AtomicInteger(0);

    private String currentRegion ="";   //当前区域
    private int mSceneCount = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void afterCreate() {

    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);

        sceneAdapter = new MainFragmentSceneAdapter(getActivity());
        getViewDataBind().mainFragmentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getViewDataBind().mainFragmentRecycler.setAdapter(sceneAdapter);
        sceneAdapter.setItems(mScenes);

    }

    @Override
    protected void initDatum() {

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

    @Override
    public void onResume() {
        super.onResume();
        if (!VcomSingleton.getInstance().getLoginUser().isEmpty()) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onMessageEvent(MessageEvent event) {
        switch (event.msgCode) {
            case MessageEvent.regionReady:
                mRegions.clear();
                mRegions.addAll(VcomSingleton.getInstance().getUserRegion());
                getViewDataBind().mainFragmentProgress.setVisibility(View.GONE);
                loadTabs();
                break;
        }
    }

    private void loadTabs() {
        if (mRegions.size() == 0) {
            //banlap: 添加显示无场景无区域时图标
            getViewDataBind().ivMainNoSmartScene.setVisibility(View.VISIBLE);
            getViewDataBind().tvMainNoSmartScene.setVisibility(View.VISIBLE);
            getViewDataBind().tvMainNoSmartScene.setText(getString(R.string.no_region));
            getViewDataBind().mainFragmentCreateRegion.setVisibility(View.VISIBLE);
            getViewDataBind().mainFragmentCreateScene.setVisibility(View.GONE);
            getViewDataBind().mainFragmentTab.setVisibility(View.GONE);
            getViewDataBind().mainFragmentAddScene.setVisibility(View.GONE);
            getViewDataBind().mainFragmentRecycler.setVisibility(View.GONE);
            return;
        }

        getViewDataBind().mainFragmentTab.removeAllTabs();
        getViewDataBind().mainFragmentTab.setVisibility(View.VISIBLE);
        //banlap: 变更主页交互功能 隐藏添加场景 (抛弃)
        //getViewDataBind().mainFragmentAddScene.setVisibility(View.INVISIBLE);
        getViewDataBind().mainFragmentAddScene.setVisibility(View.VISIBLE);
        getViewDataBind().mainFragmentCreateRegion.setVisibility(View.GONE);
        //banlap: 添加显示无场景无区域时图标
        getViewDataBind().ivMainNoSmartScene.setVisibility(View.GONE);
        getViewDataBind().tvMainNoSmartScene.setVisibility(View.GONE);

        for (int i = 0; i < mRegions.size(); i++) {
            Region region = mRegions.get(i);
            TabLayout.Tab tab = getViewDataBind().mainFragmentTab.newTab();
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_fragment_region, null);
            tab.setCustomView(view);
            TextView tvTitle = view.findViewById(R.id.item_main_fragment_region_name);
            tvTitle.setText(region.getSpaceName());
            getViewDataBind().mainFragmentTab.addTab(tab, false);
        }

        getViewDataBind().mainFragmentTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                regionIndex.set(tab.getPosition());
                Region region = mRegions.get(tab.getPosition());
                refreshSceneData(region);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (regionIndex.get() >= mRegions.size()) {
            regionIndex.set(0);
        }
        //banlap: 获取当前区域的场景数量
        mSceneCount = mRegions.get(regionIndex.get()).getSceneList().size();
        refreshSceneData(mRegions.get(regionIndex.get()));
        getViewDataBind().mainFragmentTab.selectTab(getViewDataBind().mainFragmentTab.getTabAt(regionIndex.get()));
    }

    public void refreshSceneData(Region region) {
        List<Scene> scenes = region.getSceneList();
        currentRegion = region.getSpaceId();
        if (scenes == null || scenes.size() == 0) {
            getViewDataBind().mainFragmentCreateScene.setVisibility(View.VISIBLE);
            //banlap: 添加显示无场景无空间时图标
            getViewDataBind().ivMainNoSmartScene.setVisibility(View.VISIBLE);
            getViewDataBind().tvMainNoSmartScene.setVisibility(View.VISIBLE);
            getViewDataBind().tvMainNoSmartScene.setText(getString(R.string.no_scene));

            getViewDataBind().mainFragmentRecycler.setVisibility(View.GONE);
            return;
        }
        getViewDataBind().mainFragmentCreateScene.setVisibility(View.GONE);
        //banlap: 添加显示无场景无空间时图标
        getViewDataBind().ivMainNoSmartScene.setVisibility(View.GONE);
        getViewDataBind().tvMainNoSmartScene.setVisibility(View.GONE);
        getViewDataBind().mainFragmentRecycler.setVisibility(View.VISIBLE);


        mScenes.clear();
        mScenes.addAll(scenes);
        sceneAdapter.notifyDataSetChanged();


    }


    //banlap: 点击场景设置
    public void goSceneManager(Scene scene) {
        if(!currentRegion.equals("")) {
            Intent intent = new Intent(getActivity(), SceneManagerActivity.class);
            intent.putExtra("sceneId", scene.getSceneId());
            intent.putExtra("regionId", currentRegion);
            startActivity(intent);
        }
    }

    //banlap: 侧滑删除场景 提示框
    public void goDeleteScene(Scene scene) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title))
                .setMessage(getString(R.string.dialog_message_delete_scene))
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                    dialog.dismiss();
                    //getViewModel().deleteScene(scene);
                    deleteSceneEquip(scene);
                })
                .create()
                .show();
    }
    //banlap: 侧滑删除场景
    public void deleteSceneEquip(Scene scene) {
        if(scene.getUserEquipList().size()>0) {
            //banlap: 根据场景里面的设备上逐步删除场景id
            for(int i=0; i<scene.getUserEquipList().size(); i++) {
                if(Integer.parseInt(scene.getUserEquipList().get(i).getMeshAddress()) != -1) {
                    int meshId = Integer.parseInt(scene.getUserEquipList().get(i).getMeshAddress());
                    byte opcode = (byte) 0xEE;
                    byte[] param = {0x00, (byte) Integer.parseInt(scene.getSceneMeshId())};
                    VcomService.getInstance().sendCommandNoResponse(opcode, meshId, param);
                }
            }
        }
        getViewModel().deleteScene(scene);
    }

    @Override
    public void goDeleteSceneSuccess() {
        Toast.makeText(getActivity(),getString(R.string.toast_delete_success),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goEditRegion() {
        //banlap: 变更主页添加按钮功能 - 显示菜单
        DialogAddMenuBinding addMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_add_menu, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(addMenuBinding.getRoot())
                .create();
        alertDialog.show();
        //banlap: 获取设备宽高
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        //banlap: 设置菜单大小长宽
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = (int)(display.getWidth()*0.4);
        //params.height = (int)(display.getHeight()*0.165);
        //params.width = 550;
        //params.height = (int)(display.getHeight()*0.16);
        params.x = (int)(display.getWidth());
        params.y = -(int)(display.getHeight()*0.36);

        alertDialog.getWindow().setAttributes(params);

        //banlap: 点击添加区域
        addMenuBinding.llAddMenuAddRoom.setOnClickListener(v->{
            alertDialog.dismiss();
            goGuideRegion();
        });
        //banlap: 点击添加场景
        addMenuBinding.llAddMenuAddScene.setOnClickListener(v->{
            alertDialog.dismiss();
            if(mRegions.size()>0){
                //goAddScene();
                goAddNewScene();
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_add_region),Toast.LENGTH_LONG).show();
            }
        });

       /* Intent goManager = new Intent(getActivity(), EditRegionActivity.class);
        startActivity(goManager);*/

    }

    @Override
    public void refreshData() {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.refreshRegion));
    }

    /*
    * banlap: 添加区域
    * */
    @Override
    public void goGuideRegion() {

        if (getActivity() == null) {
            return;
        }

        guideRegion.clear();

        //banlap: 变更添加区域向导方式
        DialogAddRegionBinding addRegionBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_add_region, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(addRegionBinding.getRoot())
                .create();
        alertDialog.show();

        //banlap: 默认填入区域名称
        int regionCount = 1;
        String regionNewName = getString(R.string.region);

        if(mRegions.size()>0){
            regionCount = mRegions.size();
            regionCount++;
        }
        regionNewName = regionNewName + regionCount;
        addRegionBinding.dialogAddRegionName.setText(regionNewName);

        addRegionBinding.dialogAddRegionImageCancel.setVisibility(View.INVISIBLE);
        addRegionBinding.dialogAddRegionCancel.setOnClickListener(v->{ alertDialog.dismiss();});
        addRegionBinding.dialogAddRegionCommit.setOnClickListener(v -> {
            String regionName = addRegionBinding.dialogAddRegionName.getText().toString();
            if(!TextUtils.isEmpty(regionName)){
                Region newRegion = new Region(regionName);
                guideRegion.add(newRegion);
                getViewModel().addRegionList(guideRegion);
                alertDialog.dismiss();
            }
        });
        //banlap: 原来的添加区域向导方式
       /* Region officeRegion = new Region("办公室");
        Region parlourRegion = new Region("会客厅");
        Region corridorRegion = new Region("走廊");

        DialogGuideAddRegionBinding guideAddRegionBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_guide_add_region, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(guideAddRegionBinding.getRoot())
                .create();
        alertDialog.show();

        guideAddRegionBinding.dialogGuideAddRegionCancel.setOnClickListener(v -> alertDialog.dismiss());

        guideAddRegionBinding.dialogGuideAddRegionOffice.setOnClickListener(view -> {
            if (guideAddRegionBinding.dialogGuideAddRegionOfficeIcon.getVisibility() == View.GONE) {
                guideRegion.add(officeRegion);
                guideAddRegionBinding.dialogGuideAddRegionOfficeIcon.setVisibility(View.VISIBLE);
            } else {
                guideRegion.remove(officeRegion);
                guideAddRegionBinding.dialogGuideAddRegionOfficeIcon.setVisibility(View.GONE);
            }
        });

        guideAddRegionBinding.dialogGuideAddRegionParlour.setOnClickListener(view -> {
            if (guideAddRegionBinding.dialogGuideAddRegionParlourIcon.getVisibility() == View.GONE) {
                guideRegion.add(parlourRegion);
                guideAddRegionBinding.dialogGuideAddRegionParlourIcon.setVisibility(View.VISIBLE);
            } else {
                guideRegion.remove(parlourRegion);
                guideAddRegionBinding.dialogGuideAddRegionParlourIcon.setVisibility(View.GONE);
            }
        });

        guideAddRegionBinding.dialogGuideAddRegionCorridor.setOnClickListener(view -> {
            if (guideAddRegionBinding.dialogGuideAddRegionCorridorIcon.getVisibility() == View.GONE) {
                guideRegion.add(corridorRegion);
                guideAddRegionBinding.dialogGuideAddRegionCorridorIcon.setVisibility(View.VISIBLE);
            } else {
                guideRegion.remove(corridorRegion);
                guideAddRegionBinding.dialogGuideAddRegionCorridorIcon.setVisibility(View.GONE);
            }
        });

        guideAddRegionBinding.dialogGuideAddRegionCommit.setOnClickListener(v -> {
            getViewModel().addRegionList(guideRegion);
            alertDialog.dismiss();
        });*/
    }

    @Override
    public void addRegionSuccess() {
        Toast.makeText(getActivity(),getString(R.string.toast_add_success),Toast.LENGTH_SHORT).show();
        refreshData();
    }
    @Override
    public void addRegionFailure() {
        Toast.makeText(getActivity(),getString(R.string.toast_add_error),Toast.LENGTH_SHORT).show();
    }


    @Override
    public void goGuideScene() {
        if (getActivity() == null) {
            return;
        }

        guideScene.clear();

        Scene scene1 = new Scene("场景1", "0");
        Scene scene2 = new Scene("场景2", "0");
        Scene scene3 = new Scene("场景3", "0");

        DialogGuideAddSceneBinding addSceneBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_guide_add_scene, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(addSceneBinding.getRoot())
                .create();
        alertDialog.show();

        addSceneBinding.dialogGuideAddSceneRegion.setText(mRegions.get(regionIndex.get()).getSpaceName());

        addSceneBinding.dialogGuideAddSceneCancel.setOnClickListener(v -> alertDialog.dismiss());

        addSceneBinding.dialogGuideAddSceneOne.setOnClickListener(view -> {
            if (addSceneBinding.dialogGuideAddSceneOneIcon.getVisibility() == View.GONE) {
                guideScene.add(scene1);
                addSceneBinding.dialogGuideAddSceneOneIcon.setVisibility(View.VISIBLE);
            } else {
                guideScene.remove(scene1);
                addSceneBinding.dialogGuideAddSceneOneIcon.setVisibility(View.GONE);
            }
        });

        addSceneBinding.dialogGuideAddSceneTwo.setOnClickListener(view -> {
            if (addSceneBinding.dialogGuideAddSceneTwoIcon.getVisibility() == View.GONE) {
                guideScene.add(scene2);
                addSceneBinding.dialogGuideAddSceneTwoIcon.setVisibility(View.VISIBLE);
            } else {
                guideScene.remove(scene2);
                addSceneBinding.dialogGuideAddSceneTwoIcon.setVisibility(View.GONE);
            }
        });

        addSceneBinding.dialogGuideAddSceneThree.setOnClickListener(view -> {
            if (addSceneBinding.dialogGuideAddSceneThreeIcon.getVisibility() == View.GONE) {
                guideScene.add(scene3);
                addSceneBinding.dialogGuideAddSceneThreeIcon.setVisibility(View.VISIBLE);
            } else {
                guideScene.remove(scene3);
                addSceneBinding.dialogGuideAddSceneThreeIcon.setVisibility(View.GONE);
            }
        });

        addSceneBinding.dialogGuideAddSceneCommit.setOnClickListener(v -> {
            getViewModel().addSceneList(mRegions.get(regionIndex.get()).getSpaceId(), guideScene);
            alertDialog.dismiss();
        });
    }

    @Override
    public void goAddScene() {
        if (getActivity() == null) {
            return;
        }
        DialogGuideAddSceneSingleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()),
                R.layout.dialog_guide_add_scene_single, null, false);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();

        Integer[] icons = {R.drawable.ic_scene_0, R.drawable.ic_scene_1, R.drawable.ic_scene_2, R.drawable.ic_scene_3};
        Integer[] iconSelected = {R.drawable.ic_scene_selected_0, R.drawable.ic_scene_selected_1, R.drawable.ic_scene_selected_2, R.drawable.ic_scene_selected_3};

        SceneIconAdapter adapter = new SceneIconAdapter(getActivity(), icons, iconSelected);
        binding.dialogGuideAddSceneSingleRegion.setText(mRegions.get(regionIndex.get()).getSpaceName());
        binding.dialogGuideAddSceneSingleCancel.setOnClickListener(v -> alertDialog.dismiss());
        binding.dialogGuideAddSceneSingleRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.dialogGuideAddSceneSingleRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.dialogGuideAddSceneSingleCommit.setOnClickListener(v -> {
            String sceneName = binding.dialogGuideAddSceneSingleEdit.getText().toString();
            if (!TextUtils.isEmpty(sceneName)) {
                getViewModel().addSingleScene(mRegions.get(regionIndex.get()).getSpaceId(), new Scene(sceneName, String.valueOf(adapter.selectIndex)));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    /*
     * banlap: 点击添加新场景 （重新设计界面）
     * */
    @Override
    public void goAddNewScene() {

        if(!currentRegion.equals("")){
            mSceneCount++;
            List<Scene> addNewScene = new ArrayList<>();
            String sceneName = getString(R.string.add_new_scene) + mSceneCount;
            Scene scene = new Scene(sceneName, "0");
            addNewScene.add(scene);
            getViewModel().addSceneList(mRegions.get(regionIndex.get()).getSpaceId(), addNewScene);

            //添加新场景
            //Intent addNewSceneIntent = new Intent(getActivity(), AddSceneActivity.class);
            //addNewSceneIntent.putExtra("CurrentRegionId", currentRegion);
            //addNewSceneIntent.putExtra("NewSceneDefaultName", sceneName);
            //startActivity(addNewSceneIntent);
        }
    }

    /*
     * banlap: 添加场景 返回成功
     * */
    @Override
    public void addSceneSuccess(List<Scene> scenes) {
        Toast.makeText(getActivity(),getString(R.string.toast_add_success),Toast.LENGTH_SHORT).show();
        //添加新场景
        Intent addNewSceneIntent = new Intent(getActivity(), AddSceneActivity.class);
        addNewSceneIntent.putExtra("CurrentRegionId", currentRegion);
        addNewSceneIntent.putExtra("NewSceneDefaultName", scenes.get(0).getSceneName());
        startActivity(addNewSceneIntent);
        refreshData();
    }

    /*
     * banlap: 添加场景 返回失败
     * */
    @Override
    public void addSceneFailure() {
        Toast.makeText(getActivity(),getString(R.string.toast_add_error),Toast.LENGTH_SHORT).show();
    }

    /*
     * banlap: 点击进入区域管理界面
     * */
    @Override
    public void goSceneManger() {
        Intent goManager = new Intent(getActivity(), EditRegionActivity.class);
        startActivity(goManager);
    }


    /*
    * banlap: 主页控制台 区域中场景列表
    * */
    private class MainFragmentSceneAdapter extends BaseBindingAdapter<Scene, ItemMainSceneBinding> {

        public MainFragmentSceneAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_main_scene;
        }

        @Override
        protected void onBindItem(ItemMainSceneBinding binding, Scene item, int i) {

            binding.itemSceneNormalName.setText(item.getSceneName());
            //banlap: 场景中有设备存在则显示特定的图标
            if(item.getUserEquipList().size()>0){
                binding.itemSceneNormalIcon.setBackgroundResource(mContext.getResources()
                        .getIdentifier("ic_scene_selected_" + item.getSceneImg(), "drawable", mContext.getPackageName()));
            } else {
                binding.itemSceneNormalIcon.setBackgroundResource(mContext.getResources()
                        .getIdentifier("ic_scene_" + item.getSceneImg(), "drawable", mContext.getPackageName()));
            }


            binding.getRoot().setOnClickListener(v -> {
                //banlap: 获取该场景的设备列表数量
                int userEquipListSize = mScenes.get(i).getUserEquipList().size();
                //banlap: 该场景有设备时执行场景
                if(userEquipListSize>0){
                    Toast.makeText(mContext,getString(R.string.toast_running) + item.getSceneName() + getString(R.string.toast_scene) + "!",Toast.LENGTH_SHORT).show();
                    int addr = 0xFFFF;
                    byte opcode = (byte) 0xEF;
                    byte[] params = {(byte) Integer.parseInt(item.getSceneMeshId())};
                    VcomService.getInstance().sendCommandNoResponse(opcode, addr, params);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_no_device_in_scene), Toast.LENGTH_LONG).show();
                }

            });
            //banlap: 点击设置按钮 动画
            Animation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(400);
            animation.setRepeatCount(0);
            animation.setFillAfter(true);
            //banlap: 点击设置按钮 动画监听
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    binding.rlSceneSetting.setEnabled(false);
                }

                @Override public void onAnimationEnd(Animation animation) {
                    binding.rlSceneSetting.setEnabled(true);
                    goSceneManager(item);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    binding.rlSceneSetting.setEnabled(false);
                }
            });

            binding.rlSceneSetting.setOnClickListener(v -> {
                //binding.itemSceneNormalSetting.clearAnimation();
                //binding.itemSceneNormalSetting.startAnimation(animation);
                goSceneManager(item);

            });
            binding.flDelete.setOnClickListener(v -> goDeleteScene(item));
        }
    }

    /*
    *  banlap: 场景选择图标
    * */
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
