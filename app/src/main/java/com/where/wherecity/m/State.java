package com.where.wherecity.m;

import java.util.List;

/**
 * Created by lenovo on 2016/5/19.
 */
public class State {
    public String code;
    public String name;

    public List<City> city;

    @Override
    public String toString() {
        return name;
    }
}
