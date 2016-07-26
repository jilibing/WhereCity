package com.where.wherecity.v;

import com.where.wherecity.m.City;
import com.where.wherecity.m.RegionWrap;
import com.where.wherecity.m.State;
import com.where.wherecity.m.Region;

import java.util.List;

/**
 * Created by lenovo on 2016/5/19.
 */
public interface UpdateWhereView {
    void onUpdateState(List<State> states);
    void onUpdateCity(List<City> cities);
    void onGetData(List<RegionWrap> regionWraps);
    void onSelectData(Region region, State state, City city);
}
