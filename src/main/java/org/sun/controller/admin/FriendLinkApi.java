package org.sun.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.sun.pojo.FriendLink;
import org.sun.response.ResponseResult;

@RestController
@RequestMapping("/admin/friend_link")
public class FriendLinkApi {
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody FriendLink friendLink){
        return null;
    }
    @DeleteMapping("/{friendLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkId") String friendLinkId){
        return null;
    }
    @PutMapping("/{friendLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendLinkId") String friendLinkId){
        return null;
    }
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendLinkId") String friendLinkId){
        return null;
    }
    @GetMapping("/list")
    public ResponseResult listFriendLinks(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }
}
