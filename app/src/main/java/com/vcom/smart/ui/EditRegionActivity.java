package com.vcom.smart.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.vcom.smart.R;
import com.vcom.smart.base.BaseBindingAdapter;
import com.vcom.smart.base.BaseMvvmActivity;
import com.vcom.smart.databinding.ActivityEditRegionBinding;
import com.vcom.smart.databinding.DialogAddRegionBinding;
import com.vcom.smart.databinding.DialogEditRegionBinding;
import com.vcom.smart.databinding.ItemEditRegionBinding;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.MessageEvent;
import com.vcom.smart.model.Region;
import com.vcom.smart.server.VcomService;
import com.vcom.smart.singleton.VcomSingleton;
import com.vcom.smart.uivm.EditRegionVM;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class EditRegionActivity extends BaseMvvmActivity<EditRegionVM, ActivityEditRegionBinding> implements EditRegionVM.EditRegionVmCallBack {

    private final List<Region> regionList = new ArrayList<>();
    private RegionListAdapter adapter;

    private static AlertDialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_region;
    }

    @Override
    protected void initViews() {
        getViewDataBind().setVm(getViewModel());
        getViewModel().setCallBack(this);

        adapter = new RegionListAdapter(this);
        getViewDataBind().editRegionRecycler.setAdapter(adapter);
        adapter.setItems(regionList);
        regionList.addAll(VcomSingleton.getInstance().getUserRegion());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initDatum() {

    }

    private class RegionListAdapter extends BaseBindingAdapter<Region, ItemEditRegionBinding> {

        public RegionListAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        protected int getLayoutId(int layoutId) {
            return R.layout.item_edit_region;
        }

        @Override
        protected void onBindItem(ItemEditRegionBinding itemEditRegionBinding, Region item, int i) {
            itemEditRegionBinding.itemEditRegionName.setText(item.getSpaceName());
            itemEditRegionBinding.getRoot().setOnClickListener(v -> editRegion(item));
        }
    }

    @Override
    public void back() {
        finish();
    }

    public void editRegion(Region region) {
        DialogEditRegionBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_edit_region, null, false);
        viewDataBinding.dialogEditRegionName.setText(region.getSpaceName());
        viewDataBinding.dialogEditRegionName.setSelection(region.getSpaceName().length());
        dialog = new AlertDialog.Builder(this)
                .setView(viewDataBinding.getRoot())
                .create();
        dialog.show();
        viewDataBinding.dialogEditRegionCancel.setOnClickListener(v -> dialog.dismiss());
        viewDataBinding.dialogEditRegionDelete.setOnClickListener(v ->
                deleteAllUserEquip(region)
        );
        viewDataBinding.dialogEditRegionUpdate.setOnClickListener(v -> {
            String regionName = viewDataBinding.dialogEditRegionName.getText().toString();
            if (TextUtils.isEmpty(regionName)) {
                return;
            }
            region.setSpaceName(regionName);
            getViewModel().updateRegion(region);
        });
    }

    @Override
    public void addRegion() {
        DialogAddRegionBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_add_region, null, false);
        dialog = new AlertDialog.Builder(this)
                .setView(viewDataBinding.getRoot())
                .create();
        dialog.show();
        viewDataBinding.dialogAddRegionCancel.setOnClickListener(v -> dialog.dismiss());
        viewDataBinding.dialogAddRegionImageCancel.setOnClickListener(v -> dialog.dismiss());
        viewDataBinding.dialogAddRegionCommit.setOnClickListener(v -> {
            String regionName = viewDataBinding.dialogAddRegionName.getText().toString();
            if (!TextUtils.isEmpty(regionName)) {
                List<Region> mNewRegionList = new ArrayList<>();
                Region newRegion = new Region(regionName);
                mNewRegionList.add(newRegion);
                getViewModel().addRegionList(mNewRegionList);
            }
            //getViewModel().requestAddRegion(regionName);
        });
    }

    @Override
    public void requestSuccess() {
        Toast.makeText(getApplication(),getString(R.string.toast_update_success),Toast.LENGTH_SHORT).show();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        getViewModel().getRegion();

    }

    @Override
    public void requestFailure() {
        Toast.makeText(getApplication(),getString(R.string.toast_update_error),Toast.LENGTH_SHORT).show();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void deleteAllUserEquip(Region region) {
        //banlap: 删除区域下所有场景的设备删除
        if(region.getSceneList() != null) {
            if(region.getSceneList().size() >0) {
                for(int i=0; i<region.getSceneList().size(); i++) {
                    if(region.getSceneList().get(i).getUserEquipList().size()>0) {
                        List<Equip> userEquipList = region.getSceneList().get(i).getUserEquipList();
                        for(int j=0; j<userEquipList.size(); j++) {
                            if(Integer.parseInt(userEquipList.get(j).getMeshAddress()) != -1) {
                                int meshId = Integer.parseInt(userEquipList.get(j).getMeshAddress());
                                byte opcode = (byte) 0xEE;
                                byte[] param = {0x00, (byte) Integer.parseInt(region.getSceneList().get(i).getSceneMeshId())};
                                VcomService.getInstance().sendCommandNoResponse(opcode, meshId, param);
                            }
                        }
                    }
                }
            }
        }
        getViewModel().deleteRegion(region);
    }

    @Override
    public void deleteSuccess(Region region) {
        Toast.makeText(getApplication(),getString(R.string.toast_delete_success),Toast.LENGTH_SHORT).show();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        getViewModel().getRegion();
    }

    @Override
    public void deleteFailure() {
        Toast.makeText(getApplication(),getString(R.string.toast_delete_error),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newRegionData(List<Region> regions) {
        regionList.clear();
        regionList.addAll(regions);
        adapter.notifyDataSetChanged();
    }
}