package org.sun.controller.portal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sun.response.ResponseResult;

@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {

    public ResponseResult doSearch(@RequestParam("keyword") String keyword, @RequestParam("page") int page){
        return null;
    }
}
