package com.barmej.driverapp.callback;

import com.barmej.driverapp.domain.entity.FullStatus;

public interface StatusCallBack {
    void onUpdate(FullStatus fullStatus);
}
