package com.where.wherecity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.where.wherecity.m.City;
import com.where.wherecity.m.Region;
import com.where.wherecity.m.RegionWrap;
import com.where.wherecity.m.State;
import com.where.wherecity.p.CityPresenter;
import com.where.wherecity.v.UpdateWhereView;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class MainActivity extends AppCompatActivity implements UpdateWhereView {

    private CityPresenter mCityPresenter;
    private WheelView mRegionWheel, mStateWheel, mCityWheel;
    private TextView tv_select_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_select_city = (TextView) findViewById(R.id.tv_select_city);

        findViewById(R.id.button_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int provinceIndex = mRegionWheel.getCurrentItem();
                int cityIndex = mStateWheel.getCurrentItem();
                int areaIndex = mCityWheel.getCurrentItem();

                mCityPresenter.getSelectData(provinceIndex, cityIndex, areaIndex);
            }
        });

        mRegionWheel = (WheelView) findViewById(R.id.wheel_region);
        mStateWheel = (WheelView) findViewById(R.id.wheel_state);
        mCityWheel = (WheelView) findViewById(R.id.wheel_city);

        mCityPresenter = new CityPresenter(this);
        mCityPresenter.attach(this);

        mCityPresenter.getWheelData();
    }

    @Override
    public void onUpdateState(List<State> states) {
        if (states == null) {
            return;
        }

        ArrayWheelAdapter<State> stateArrayWheelAdapter = new ArrayWheelAdapter<State>(this, states.toArray(new State[0]));
        mStateWheel.setViewAdapter(stateArrayWheelAdapter);
        mStateWheel.setCurrentItem(0);
    }

    @Override
    public void onUpdateCity(List<City> cities) {
        if (cities == null) {
            return;
        }

        ArrayWheelAdapter<City> cityArrayWheelAdapter = new ArrayWheelAdapter<City>(this, cities.toArray(new City[0]));
        mCityWheel.setViewAdapter(cityArrayWheelAdapter);
        mCityWheel.setCurrentItem(0);
    }

    @Override
    public void onGetData(List<RegionWrap> regionWraps) {
        initWheelData(regionWraps);
    }

    @Override
    public void onSelectData(Region region, State state, City city) {
        if (region == null) {
            return;
        }

        if (state == null) {
            return;
        }

        if (city == null) {
            Toast.makeText(MainActivity.this, "省:" + region.code + " 市:" + state.code, Toast.LENGTH_SHORT).show();
            tv_select_city.setText("省 code:" + region.code + " name:" + region.name + "\n市 code:" + state.code + " name:" + state.name);
        } else {
            Toast.makeText(MainActivity.this, "省:" + region.code + " 市:" + state.code + " 区:" + city.code, Toast.LENGTH_SHORT).show();
            tv_select_city.setText("省 code:" + region.code + "name:" + region.name + "\n市 code:" + state.code + " name:" + state.name + "\n区 code:" + city.code + " name:" + city.name);
        }
    }

    private void initWheelData(final List<RegionWrap> regionWraps) {
        if (regionWraps == null || regionWraps.size() <= 0) {
            return;
        }

        ArrayWheelAdapter<RegionWrap> regionWrapArrayWheelAdapter = new ArrayWheelAdapter<RegionWrap>(this, regionWraps.toArray(new RegionWrap[0]));
        mRegionWheel.setViewAdapter(regionWrapArrayWheelAdapter);

        mRegionWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (newValue < 0 || newValue >= regionWraps.size()) {
                    return;
                }
                mCityPresenter.updateStateData(regionWraps.get(newValue).region.state);
                mCityPresenter.updateCityData(regionWraps.get(newValue).region.state.get(0).city);
            }
        });

        mRegionWheel.addClickingListener(new OnWheelClickedListener() {
            @Override
            public void onItemClicked(WheelView wheel, int newValue) {
                mRegionWheel.setCurrentItem(newValue);
            }
        });

        mStateWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int provinceIndex = mRegionWheel.getCurrentItem();
                if (newValue < 0 || newValue >= regionWraps.get(provinceIndex).region.state.size()) {
                    return;
                }
                mCityPresenter.updateCityData(regionWraps.get(provinceIndex).region.state.get(newValue).city);
            }
        });

        // 默认选择市第一项
        mStateWheel.addClickingListener(new OnWheelClickedListener() {
            @Override
            public void onItemClicked(WheelView wheel, int newValue) {
                mStateWheel.setCurrentItem(newValue);
            }
        });

        // 默认选择区第一项
        mCityPresenter.updateStateData(regionWraps.get(0).region.state);
        if (0 >= regionWraps.size()) {
            return;
        }
        mCityPresenter.updateCityData(regionWraps.get(0).region.state.get(0).city);
    }

    @Override
    protected void onDestroy() {
        if (mCityPresenter != null) {
            mCityPresenter.detach();
        }
        super.onDestroy();
    }
}
