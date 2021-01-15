package org.sun.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Looper;
import org.sun.response.ResponseResult;
import org.sun.services.ILoopService;

@RestController
@RequestMapping("/admin/loop")
public class LooperAdminApi {

    @Autowired
    private ILoopService loopService;

    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper){
        return loopService.addLoop(looper);
    }


    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{loopId}")
    public ResponseResult deleteLooper(@PathVariable("loopId") String loopId){
        return loopService.deleteLoop(loopId);
    }

    @PreAuthorize("@permission.admin()")
    @PutMapping("/{loopId}")
    public ResponseResult updateLooper(@PathVariable("loopId") String loopId, @RequestBody Looper looper){
        return loopService.updateLoop(loopId, looper);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/{loopId}")
    public ResponseResult getLooper(@PathVariable("loopId") String loopId){
        return loopService.getLoop(loopId);
    }

    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listLoops(){
        return loopService.listLoop();
    }
}
