package com.wy313.service.impl;

import com.wy313.entity.Page;
import com.wy313.service.iDownService;
import com.wy313.tools.PageDown;

public class BQGDownService implements iDownService {
    @Override
    public Page down(String url) {
        Page page = new Page();
        page.setUrl(url);
        page.setConetnt(PageDown.downinfo(url));
        return page;
    }
}
