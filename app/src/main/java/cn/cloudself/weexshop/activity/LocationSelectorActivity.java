package cn.cloudself.weexshop.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.location.demo.CheckPermissionsActivity;
import com.amap.searchdemo.SegmentedGroup;

import java.util.ArrayList;
import java.util.List;

import cn.cloudself.weexshop.R;
import cn.cloudself.weexshop.adapter.SearchResultAdapter;
import cn.cloudself.weexshop.weex.module.WXAMap;

/**
 * 修改自：amap-demo/android-place-choose
 * https://github.com/amap-demo/android-place-choose
 */
public class LocationSelectorActivity extends CheckPermissionsActivity implements
        GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {

    // 顶部的搜索栏
    private AutoCompleteTextView searchText;

    private AMap aMap;
    private MapView mapView;

    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;

    private PoiSearch.Query query; // Poi查询条件类

    private String searchType;
    private LatLonPoint searchLatlonPoint;


    private List<PoiItem> resultData = new ArrayList<>();

    private SearchResultAdapter searchResultAdapter;

    private List<Tip> autoTips;
    private boolean isfirstinput = true;
    private PoiItem firstItem;

    private static final String TAG = "LocationSelectorActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_location_selector);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);


        initAMap();
        initView();

        resultData = new ArrayList<>();

    }


    /**
     * 获得了所有权限后
     * 最主要是定位权限
     */
    @Override
    protected void onAllPermissionsGetted() {
        Log.d(TAG, "onAllPermissionsGetted: 定位权限已获得");
        initView();
    }

    private void initView() {

        // 显示搜索结果
        ListView listView = (ListView) findViewById(R.id.listview);
        // 。。。
        searchResultAdapter = new SearchResultAdapter(LocationSelectorActivity.this);
        listView.setAdapter(searchResultAdapter);
        // 点击地址事件
        listView.setOnItemClickListener(onItemClickListener);

        // 为分段选择控件添加事件
        SegmentedGroup mSegmentedGroup = (SegmentedGroup) findViewById(R.id.segmented_group);
        mSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                String[] items = {"住宅区", "学校", "楼宇", "商场"};
                searchType = items[0];
                switch (checkedId) {
                    case R.id.radio0:
                        searchType = items[0];
                        break;
                    case R.id.radio1:
                        searchType = items[1];
                        break;
                    case R.id.radio2:
                        searchType = items[2];
                        break;
                    case R.id.radio3:
                        searchType = items[3];
                        break;
                    default:
                        searchType = items[0];
                }
                geoAddress();
            }
        });

        // 为搜索栏添加事件
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                if (newText.length() > 0) {
                    // 关键字搜索的参数
                    InputtipsQuery inputquery = new InputtipsQuery(newText, "宁波");
                    // 关键字搜索的 提示
                    Inputtips inputTips = new Inputtips(LocationSelectorActivity.this, inputquery);
                    inputquery.setCityLimit(true);
                    inputTips.setInputtipsListener(inputtipsListener);
                    inputTips.requestInputtipsAsyn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 选择了某个地址
        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (autoTips != null && autoTips.size() > position) {
                    Tip tip = autoTips.get(position);
                    searchPoi(tip);
                }
            }
        });

        // 描述信息与坐标的转换
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        // 应该是正在加载提示
        progDialog = new ProgressDialog(this);

        // 关闭软键盘
        hideSoftKey(searchText);
    }

    // 上方的 关键字提示返回结果 转poi
    private void searchPoi(Tip result) {
        String inputSearchKey = result.getName();
        searchLatlonPoint = result.getPoint();
        firstItem = new PoiItem("tip", searchLatlonPoint, inputSearchKey, result.getAddress());
        firstItem.setCityName(result.getDistrict());
        firstItem.setAdName("");
        resultData.clear();

        searchResultAdapter.setSelectedPosition(0);

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(searchLatlonPoint.getLatitude(), searchLatlonPoint.getLongitude()), 16f)
        );

        hideSoftKey(searchText);

        // 刷新下方的列表
        doSearchQuery();
    }


    /**
     * 开始进行poi搜索（附近搜索）(搜索所有东西)
     */
    protected void doSearchQuery() {
        String searchKey = "";
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(searchKey, searchType, "");
        query.setCityLimit(true);
        query.setPageSize(20);
        query.setPageNum(0);

        /*
         * 开始搜索
         * @see #onPoiSearched 搜索成功回调
         */
        if (searchLatlonPoint != null) {
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            // 周边检索 设置中心点信息 半径为1000（单位好像是米）
            poiSearch.setBound(new PoiSearch.SearchBound(searchLatlonPoint, 1000, true));
            poiSearch.searchPOIAsyn();
        }
    }

    /**
     * 初始化 AMap
     */
    private void initAMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 改变缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        /*
         * 初始化小蓝点
         */
        //初始化定位蓝点样式类
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        //设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);

        // 地图状态发生变化，包括地图中心点位置改变等
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                Log.d(TAG, "onCameraChangeFinish: 地图有变化");
                searchLatlonPoint = new LatLonPoint(
                        cameraPosition.target.latitude,
                        cameraPosition.target.longitude);
                geoAddress();
            }
        });

        // 改变缩放级别
//        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
//            @Override
//            public void onMapLoaded() {
//                Log.d(TAG, "onMapLoaded: 地图加载完毕");
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        aMap.getCameraPosition().target, 16f)
//                );
//            }
//        });

    }

    /**
     * 响应逆地理编码
     */
    public void geoAddress() {
        showLoadingDialog();
        searchText.setText("");
        if (searchLatlonPoint != null) {
            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(searchLatlonPoint, 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        }
    }

    /**
     * @see #geoAddress()
     * 坐标转地址请求成功返回时
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissLoadingDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String address = result.getRegeocodeAddress().getProvince()
                        + result.getRegeocodeAddress().getCity()
                        + result.getRegeocodeAddress().getDistrict()
                        + result.getRegeocodeAddress().getTownship();
                firstItem = new PoiItem("regeo", searchLatlonPoint, address, address);
                doSearchQuery();
            }
        } else {
            Toast.makeText(
                    LocationSelectorActivity.this,
                    "error code is " + rCode,
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * POI搜索结果回调
     * 附近信息搜索结果成功返回
     *
     * @param poiResult  搜索结果
     * @param resultCode 错误码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {
                if (poiResult.getQuery().equals(query)) {
                    List<PoiItem> poiItems = poiResult.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
                        updateListview(poiItems);
                    } else {
                        Toast.makeText(
                                LocationSelectorActivity.this,
                                "无搜索结果",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(
                        LocationSelectorActivity.this,
                        "无搜索结果",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 更新列表中的item
     */
    private void updateListview(List<PoiItem> poiItems) {
        resultData.clear();
        searchResultAdapter.setSelectedPosition(0);
        resultData.add(firstItem);
        resultData.addAll(poiItems);

        searchResultAdapter.setData(resultData);
        searchResultAdapter.notifyDataSetChanged();
    }

    // 选择了最终的地址
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PoiItem poiItem = (PoiItem) searchResultAdapter.getItem(position);
            WXAMap.setLocation(poiItem);
            backToShopActivity();
        }
    };

    /**
     * 返回主界面
     */
    private void backToShopActivity() {
        Intent intent = new Intent(this, ShopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // 附近搜索提示 搜索完毕
    Inputtips.InputtipsListener inputtipsListener = new Inputtips.InputtipsListener() {
        @Override
        public void onGetInputtips(List<Tip> list, int rCode) {
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
                autoTips = list;
                List<String> listString = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    listString.add(list.get(i).getName());
                }
                ArrayAdapter<String> aAdapter = new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.route_inputs, listString);
                searchText.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
                if (isfirstinput) {
                    isfirstinput = false;
                    searchText.showDropDown();
                }
            } else {
                Toast.makeText(
                        LocationSelectorActivity.this,
                        "erroCode " + rCode,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKey(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 展示
     * 正在加载提示框
     */
    public void showLoadingDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在加载...");
        progDialog.show();
    }

    /**
     * 销毁
     * 正在加载对话框
     */
    public void dismissLoadingDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
