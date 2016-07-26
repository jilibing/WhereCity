package com.where.wherecity.p;

import android.content.Context;

import com.google.gson.Gson;
import com.where.wherecity.m.City;
import com.where.wherecity.m.ProvinceModel;
import com.where.wherecity.m.Region;
import com.where.wherecity.m.RegionWrap;
import com.where.wherecity.m.State;
import com.where.wherecity.v.UpdateWhereView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/19.
 */
public class CityPresenter {

    private Context mContext;
    private UpdateWhereView updateWhereView;
    private List<RegionWrap> regionWraps = new ArrayList<>();

    public CityPresenter(Context context) {
        mContext = context;
    }

    public void attach(UpdateWhereView updateWhereView) {
        this.updateWhereView = updateWhereView;
    }

    public void detach() {
        this.updateWhereView = null;
    }

    public void getWheelData() {
        if (updateWhereView == null) {
            return;
        }

        String cityData = getCityDataString();
        if (cityData == null) {
            return;
        }

        Gson gson = new Gson();
        try {
            ProvinceModel provinceModel = gson.fromJson(cityData, ProvinceModel.class);
            regionWraps = provinceModel.provinces;
            updateWhereView.onGetData(provinceModel.provinces);
        } catch (Exception e) {
        }

    }

    private String getCityDataString() {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open("city.json");
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateStateData(List<State> states) {
        if (updateWhereView == null) {
            return;
        }

        updateWhereView.onUpdateState(states);
    }

    public void updateCityData(List<City> cities) {
        if (updateWhereView == null) {
            return;
        }

        updateWhereView.onUpdateCity(cities);
    }

    public void getSelectData(int reginIndex, int stateIndex, int cityIndex) {
        if (updateWhereView == null) {
            return;
        }

        if (reginIndex < 0 || reginIndex >= regionWraps.size()) {
            return;
        }

        Region region = regionWraps.get(reginIndex).region;

        //
        State state = regionWraps.get(reginIndex).region.state.get(stateIndex);
        if (stateIndex > 0 && stateIndex < regionWraps.get(reginIndex).region.state.size()) {
            state = regionWraps.get(reginIndex).region.state.get(stateIndex);
        }

        //
        List<City> cities = regionWraps.get(reginIndex).region.state.get(stateIndex).city;
        City city = null;
        if(cities == null || cities.size()<=0) {

        }else {
            city = cities.get(cityIndex);
            if (cityIndex > 0 && cityIndex < regionWraps.get(reginIndex).region.state.get(stateIndex).city.size()) {
                city = regionWraps.get(reginIndex).region.state.get(stateIndex).city.get(cityIndex);
            }
        }

        updateWhereView.onSelectData(region, state, city);
    }
}
