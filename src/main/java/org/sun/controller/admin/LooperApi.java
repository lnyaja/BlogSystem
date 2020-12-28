package org.sun.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Looper;
import org.sun.response.ResponseResult;

@RestController
@RequestMapping("/admin/loop")
public class LooperApi {

    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper){
        return null;
    }
    @DeleteMapping("/{loopId}")
    public ResponseResult deleteLooper(@PathVariable("loopId") String loopId){
        return null;
    }
    @PutMapping("/{looperId}")
    public ResponseResult updateLooper(@PathVariable("loopId") String loopId){
        return null;
    }
    @GetMapping("/{loopId}")
    public ResponseResult getLooper(@PathVariable("loopId") String loopId){
        return null;
    }
    @GetMapping("/list")
    public ResponseResult listLoops(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }
}
